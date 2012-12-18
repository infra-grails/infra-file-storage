package ru.mirari.infra.file

/**
 * @author alari
 * @since 12/18/12 6:44 PM
 */
class BasicFilesHolder extends AbstractFilesHolder {

    private final FileStorage storage

    @Override
    protected FileStorage getStorage() {
        storage
    }

    BasicFilesHolder(FileStorage storage, String path, String bucket = null) {
        this.path = path
        this.bucket = bucket
        this.storage = storage
    }

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
     * Allowed files extensions
     */
    String[] allowedExtensions
}
