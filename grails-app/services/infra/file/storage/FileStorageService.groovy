package infra.file.storage

import infra.file.storage.domain.DomainFilesManager
import infra.file.storage.domain.FileDomainRepoProvider
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.web.multipart.MultipartFile

class FileStorageService implements ApplicationContextAware {

    static transactional = false

    private Map<String, FileStorage> storages = [:]
    private FileStorage defaultStorage

    String defaultStorageName

    def grailsApplication
    FileDomainRepoProvider fileDomainRepoProvider

    @Override
    void setApplicationContext(ApplicationContext applicationContext) throws org.springframework.beans.BeansException {
        for (FileStorage fs : applicationContext.getBeansOfType(FileStorage).values()) {
            storages.put(fs.name, fs)
        }
        defaultStorageName = grailsApplication.config.plugin.infraFileStorage.defaultStorageName ?: "local"
        defaultStorage = storages.get(defaultStorageName)
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
        getManager(domain).store(file, filename)
    }

    /**
     * Stores a file for @FilesHolder-annotated domain from multipart request
     *
     * @param domain
     * @param file
     * @param filename
     */
    void store(def domain, final MultipartFile file, String filename = null) {
        getManager(domain).store(file, filename)
    }

    /**
     * Checks if a file is uploaded for domain
     *
     * @param domain
     * @param filename
     * @return
     */
    boolean exists(def domain, String filename) {
        getManager(domain).exists(filename)
    }

    /**
     * Deletes all @FilesHolder-annotated domains' files
     *
     * @param domain
     */
    void delete(def domain) {
        getManager(domain).delete()
    }

    /**
     * Deletes a file for a @FilesHolder-annotated domain
     *
     * @param domain
     * @param filename
     */
    void delete(def domain, String filename) {
        getManager(domain).delete(filename)
    }

    /**
     * Returns an accessible url string for a domain' file
     *
     * @param domain
     * @param filename
     * @return
     */
    String getUrl(def domain, String filename = null) {
        getManager(domain).getUrl(filename)
    }

    /**
     * Builds a new Basic Files Holder object for path and bucket
     * @param path
     * @param bucket
     * @return
     */
    FilesManager getManager(String path, String bucket = null, boolean enableFileDomains = true) {
        FilesManager m = new BasicFilesManager(defaultStorage, path, bucket)
        enableFileDomains ? new DomainFilesManager(m, fileDomainRepoProvider) : m
    }

    /**
     * Builds wrapper for @FilesHolder-annotated domain object
     *
     * @param domain
     * @return
     */
    FilesManager getManager(def domain, FilesHolder holderAnnotation = null) {
        FilesManager m = new AnnotatedFilesManager(domain, this, holderAnnotation)
        m.fileDomainsEnabled ? new DomainFilesManager(m, fileDomainRepoProvider) : m
    }
}
