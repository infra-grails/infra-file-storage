package infra.file.storage

import groovy.transform.CompileStatic
import org.springframework.web.multipart.MultipartFile

/**
 * @author alari
 * @since 12/18/12 6:16 PM
 */
@CompileStatic
abstract class AbstractFilesManager implements FilesManager {

    File getFile(String filename=null) {
        if (!filename) {
            if (getFileNames().size() == 1) filename = getFileNames().first()
        }
        getStorage().getFile(getPath(), filename, getBucket())
    }

    /**
     * Returns an accessible url for a file
     * @param filename
     * @return
     */
    String getUrl(String filename = null) {
        if (!filename) {
            if (getFileNames().size() == 1) filename = getFileNames().first()
        }
        if (!filename) {
            throw new IllegalArgumentException("Null filename is allowed only when domain (already) holds only a single file")
        }
        getStorage().getUrl(getPath(), filename, getBucket())
    }

    /**
     * Deletes a file
     * @param filename
     */
    void delete(String filename) {
        getStorage().delete(getPath(), filename, getBucket())
        getFileNames().remove(filename)
    }

    /**
     * Deletes all the holder files
     */
    void delete() {
        for (String filename : getFileNames()) {
            getStorage().delete(getPath(), filename, getBucket())
        }
        setFileNames([])
    }

    /**
     * Checks if a file exists for files holder
     * @param filename
     * @return
     */
    boolean exists(String filename) {
        getStorage().exists(getPath(), filename, getBucket())
    }

    /**
     * Stores a regular file for holder
     * @param file
     * @param filename
     * @return actual filename
     */
    String store(File file, String filename = null) {
        checkFile(file)
        filename = getStorage().store(file, getPath(), filename, getBucket())
        getFileNames().add filename
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
        filename = getStorage().store(file, getPath(), filename, getBucket())
        getFileNames().add filename
        filename
    }

    long getSize(String filename) {
        getStorage().getSize(getPath(), filename, getBucket());
    }

    private void checkFile(final MultipartFile file) {
        String extension = file.originalFilename.substring(file.originalFilename.lastIndexOf(".") + 1)
        if (getAllowedExtensions() && !getAllowedExtensions().contains(extension)) {
            throw new IllegalArgumentException("Wrong file extension")
        }
    }

    private void checkFile(final File file) {
        String extension = file.name.substring(file.name.lastIndexOf(".") + 1)
        if (getAllowedExtensions() && !getAllowedExtensions().contains(extension)) {
            println extension
            println getAllowedExtensions()
            throw new IllegalArgumentException("Wrong file extension")
        }
    }
}
