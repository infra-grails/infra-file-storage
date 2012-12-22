package ru.mirari.infra

import grails.util.Environment
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.web.multipart.MultipartFile
import ru.mirari.infra.file.AnnotatedFilesHolder
import ru.mirari.infra.file.BasicFilesHolder
import ru.mirari.infra.file.FileStorage

class FileStorageService implements ApplicationContextAware {

    static transactional = false

    private Map<String, FileStorage> storages = [:]
    private FileStorage defaultStorage

    private String deployedStorageName = "s3"
    private String developmentStorageName = "local"

    @Override
    void setApplicationContext(ApplicationContext applicationContext) throws org.springframework.beans.BeansException {
        for (FileStorage fs : applicationContext.getBeansOfType(FileStorage).values()) {
            storages.put(fs.name, fs)
        }
        defaultStorage = storages.get(Environment.isWarDeployed() ? deployedStorageName : developmentStorageName)
        assert defaultStorage
    }

    FileStorage getFileStorage(String name = null) {
        name ? storages.get(name) : defaultStorage
    }

    /**
     * Stores a file for @FilesHolder-annotated domain
     *
     * @param domain
     * @param file
     * @param filename
     */
    void store(def domain, final File file, String filename = null) {
        getHolder(domain).store(file, filename)
    }

    /**
     * Stores a file for @FilesHolder-annotated domain from multipart request
     *
     * @param domain
     * @param file
     * @param filename
     */
    void store(def domain, final MultipartFile file, String filename = null) {
        getHolder(domain).store(file, filename)
    }

    /**
     * Checks if a file is uploaded for domain
     *
     * @param domain
     * @param filename
     * @return
     */
    boolean exists(def domain, String filename) {
        getHolder(domain).exists(filename)
    }

    /**
     * Deletes all @FilesHolder-annotated domains' files
     *
     * @param domain
     */
    void delete(def domain) {
        getHolder(domain).delete()
    }

    /**
     * Deletes a file for a @FilesHolder-annotated domain
     *
     * @param domain
     * @param filename
     */
    void delete(def domain, String filename) {
        getHolder(domain).delete(filename)
    }

    /**
     * Returns an accessible url string for a domain' file
     *
     * @param domain
     * @param filename
     * @return
     */
    String getUrl(def domain, String filename = null) {
        getHolder(domain).getUrl(filename)
    }

    /**
     * Builds a new Basic Files Holder object for path and bucket
     * @param path
     * @param bucket
     * @return
     */
    BasicFilesHolder getHolder(String path, String bucket = null) {
        new BasicFilesHolder(defaultStorage, path, bucket)
    }

    /**
     * Builds wrapper for @FilesHolder-annotated domain object
     *
     * @param domain
     * @return
     */
    AnnotatedFilesHolder getHolder(def domain) {
        new AnnotatedFilesHolder(domain, this, null)
    }
}
