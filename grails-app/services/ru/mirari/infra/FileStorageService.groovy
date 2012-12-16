package ru.mirari.infra

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.multipart.MultipartFile
import ru.mirari.infra.file.FileStorage
import ru.mirari.infra.file.FilesHolder
import ru.mirari.infra.file.FilesHolderHelper

class FileStorageService implements FileStorage {

    static transactional = false

    @Delegate FileStorage fileStorage
    @Autowired
    FilesHolderHelper filesHolderHelper

    /**
     * Stores a file for @FilesHolder-annotated domain
     *
     * @param domain
     * @param file
     * @param filename
     */
    void store(def domain, final File file, String filename = null) {
        FilesHolder holder = filesHolderHelper.getHolder(domain)

        if (!filename) filename = file.name

        storeFile(holder, domain, file, filename)
    }

    /**
     * Stores a file for @FilesHolder-annotated domain from multipart request
     *
     * @param domain
     * @param file
     * @param filename
     */
    void store(def domain, final MultipartFile file, String filename = null) {
        FilesHolder holder = filesHolderHelper.getHolder(domain)

        if (!filename) filename = file.originalFilename

        storeFile(holder, domain, file, filename)
    }

    /**
     * Checks if a file is uploaded for domain
     *
     * @param domain
     * @param filename
     * @return
     */
    boolean exists(def domain, String filename) {
        FilesHolder holder = filesHolderHelper.getHolder(domain)
        fileStorage.exists(filesHolderHelper.getPath(holder, domain), filename, filesHolderHelper.getBucket(holder, domain))
    }

    private void storeFile(FilesHolder holder, def domain, final def file, String filename) {
        filesHolderHelper.checkFile(holder, file)

        fileStorage.store(file, filesHolderHelper.getPath(holder, domain), filename ?: "", filesHolderHelper.getBucket(holder, domain))

        Collection<String> fileNames = filesHolderHelper.getFileNames(holder, domain) ?: []
        if (!fileNames.contains(filename)) fileNames.add(filename)

        filesHolderHelper.setFileNames(holder, domain, fileNames)
    }

    /**
     * Deletes all @FilesHolder-annotated domains' files
     *
     * @param domain
     */
    void delete(def domain) {
        FilesHolder holder = filesHolderHelper.getHolder(domain)
        Collection<String> fileNames = filesHolderHelper.getFileNames(holder, domain)
        if (fileNames) {
            for (String filename : fileNames) {
                fileStorage.delete(filesHolderHelper.getPath(holder, domain), filename, filesHolderHelper.getBucket(holder, domain))
            }
        }
        filesHolderHelper.setFileNames(holder, domain, [])
    }

    /**
     * Deletes a file for a @FilesHolder-annotated domain
     *
     * @param domain
     * @param filename
     */
    void delete(def domain, String filename) {
        FilesHolder holder = filesHolderHelper.getHolder(domain)
        Collection<String> fileNames = filesHolderHelper.getFileNames(holder, domain)

        if (filename in fileNames) {
            fileStorage.delete(filesHolderHelper.getPath(holder, domain), filename, filesHolderHelper.getBucket(holder, domain))
        }
        fileNames.remove(filename)
        filesHolderHelper.setFileNames(holder, domain, fileNames)
    }

    /**
     * Returns an accessible url string for a domain' file
     *
     * @param domain
     * @param filename
     * @return
     */
    String getUrl(def domain, String filename = null) {
        FilesHolder holder = filesHolderHelper.getHolder(domain)
        if (!filename) {
            Collection<String> fileNames = filesHolderHelper.getFileNames(holder, domain)
            if (fileNames.size() == 1) filename = fileNames.first()
        }
        if (!filename) {
            throw new IllegalArgumentException("Null filename is allowed only when domain (already) holds only a single file")
        }
        fileStorage.getUrl(filesHolderHelper.getPath(holder, domain), filename, filesHolderHelper.getBucket(holder, domain))
    }

    /**
     * Builds a new Basic Files Holder object for path and bucket
     * @param path
     * @param bucket
     * @return
     */
    BasicFilesHolder getHolder(String path, String bucket = null) {
        new BasicFilesHolder(path, bucket)
    }

    /**
     * Helper class -- wraps basic FilesHolder functionality
     */
    @FilesHolder(
    path = { path },
    bucket = { bucket }
    )
    public class BasicFilesHolder {
        /**
         * Path where to store files -- must be unique for each holder
         */
        String path
        /**
         * Bucket where to store files to (Amazon S3 bucket, or folder for local storage, etc)
         */
        String bucket
        /**
         * List of currently stored filenames
         */
        Collection<String> fileNames = []

        /**
         * Builds a holder for a unique path
         * @param path
         */
        BasicFilesHolder(String path) {
            this.path = path
        }

        /**
         * Builds a holder for its path and bucket
         * @param path
         * @param bucket
         */
        BasicFilesHolder(String path, String bucket) {
            this.path = path
            this.bucket = bucket
        }

        /**
         * Returns an accessible url for a file
         * @param filename
         * @return
         */
        String getUrl(String filename) {
            getUrl(this, filename)
        }

        /**
         * Deletes a file
         * @param filename
         */
        void delete(String filename) {
            delete(this, filename)
        }

        /**
         * Deletes all the holder files
         */
        void delete() {
            delete(this)
        }

        /**
         * Checks if a file exists for files holder
         * @param filename
         * @return
         */
        boolean exists(String filename) {
            exists(this, filename)
        }

        /**
         * Stores a regular file for holder
         * @param file
         * @param filename
         */
        void store(File file, String filename = null) {
            store(this, file, filename)
        }

        /**
         * Stores a file for multipart request
         * @param file
         * @param filename
         */
        void store(MultipartFile file, String filename = null) {
            store(this, file, filename)
        }
    }
}
