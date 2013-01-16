package infra.file.storage

import org.springframework.web.multipart.MultipartFile

/**
 * @author alari
 * @since 12/18/12 6:16 PM
 */
abstract class AbstractFilesManager implements FilesManager {

    /**
     * Returns an accessible url for a file
     * @param filename
     * @return
     */
    String getUrl(String filename = null) {
        if (!filename) {
            if (fileNames.size() == 1) filename = fileNames.first()
        }
        if (!filename) {
            throw new IllegalArgumentException("Null filename is allowed only when domain (already) holds only a single file")
        }
        storage.getUrl(path, filename, bucket)
    }

    /**
     * Deletes a file
     * @param filename
     */
    void delete(String filename) {
        storage.delete(path, filename, bucket)
        fileNames.remove(filename)
    }

    /**
     * Deletes all the holder files
     */
    void delete() {
        for (String filename : fileNames) {
            storage.delete(path, filename, bucket)
        }
        fileNames = []
    }

    /**
     * Checks if a file exists for files holder
     * @param filename
     * @return
     */
    boolean exists(String filename) {
        storage.exists(path, filename, bucket)
    }

    /**
     * Stores a regular file for holder
     * @param file
     * @param filename
     * @return actual filename
     */
    String store(File file, String filename = null) {
        checkFile(file)
        filename = storage.store(file, path, filename, bucket)
        fileNames.add filename
        filename
    }

    /**
     * Stores a file for multipart request
     * @param file
     * @param filename
     * @return actual filename
     */
    String store(MultipartFile file, String filename = null) {
        checkFile(file)
        filename = storage.store(file, path, filename, bucket)
        fileNames.add filename
        filename
    }

    long getSize(String filename) {
        getStorage().getSize(path, filename, bucket);
    }

    private void checkFile(final MultipartFile file) {
        String extension = file.originalFilename.substring(file.originalFilename.lastIndexOf(".") + 1)
        if (allowedExtensions && !(extension in allowedExtensions)) {
            throw new IllegalArgumentException("Wrong file extension")
        }
    }

    private void checkFile(final File file) {
        String extension = file.name.substring(file.name.lastIndexOf(".") + 1)
        if (allowedExtensions && !(extension in allowedExtensions)) {
            throw new IllegalArgumentException("Wrong file extension")
        }
    }
}
