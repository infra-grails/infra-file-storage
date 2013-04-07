package infra.file.storage

import groovy.transform.CompileStatic
import infra.file.storage.ex.StorageException
import org.springframework.web.multipart.MultipartFile

/**
 * @author alari
 * @since 12/18/12 6:16 PM
 */
@CompileStatic
abstract class AbstractFilesManager implements FilesManager {

    File getFile(String filename = null) {
        if (!filename) {
            if (getFileNames().size() == 1) filename = getFileNames().first()
        }
        try {
            getStorage().getFile(getPath(), filename, getBucket())
        } catch (StorageException e) {
            e.printStackTrace()
        }
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
        try {
            getStorage().delete(getPath(), filename, getBucket())
        } catch (StorageException e) {
            e.printStackTrace()
        }
        getFileNames().remove(filename)
    }

    /**
     * Deletes all the holder files
     */
    void delete() {
        for (String filename : getFileNames()) {
            try {
                getStorage().delete(getPath(), filename, getBucket())
            } catch (StorageException e) {
                e.printStackTrace()
            }
        }
        setFileNames([])
    }

    /**
     * Checks if a file exists for files holder
     * @param filename
     * @return
     */
    boolean exists(String filename) {
        try {
            getStorage().exists(getPath(), filename, getBucket())
        } catch (StorageException e) {
            e.printStackTrace()
        }
    }

    /**
     * Stores a regular file for holder
     * @param file
     * @param filename
     * @return actual filename
     */
    String store(File file, String filename = null) {
        checkFile(file)
        try {
            filename = getStorage().store(file, getPath(), filename, getBucket())
        } catch (StorageException e) {
            e.printStackTrace()
        }
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
        try {
            filename = getStorage().store(file, getPath(), filename, getBucket())
        } catch (StorageException e) {
            e.printStackTrace()
        }
        getFileNames().add filename
        filename
    }

    long getSize(String filename) {
        try {
            return getStorage().getSize(getPath(), filename, getBucket())
        } catch (StorageException e) {
            e.printStackTrace()
        }
        0
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
