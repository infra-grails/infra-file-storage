package infra.file.storage

import org.springframework.web.multipart.MultipartFile

/**
 * @author alari
 * @since 1/15/13 9:58 PM
 */
public interface FilesManager {
    abstract public FileStorage getStorage()

    abstract public String getPath()

    abstract public String getBucket()

    abstract public Collection<String> getFileNames()

    abstract public void setFileNames(Collection<String> fileNames)

    abstract public String[] getAllowedExtensions()

    String getUrl(String filename)

    String getUrl()

    /**
     * Deletes a file
     * @param filename
     */
    void delete(String filename)

    /**
     * Deletes all the holder files
     */
    void delete()

    /**
     * Checks if a file exists for files holder
     * @param filename
     * @return
     */
    boolean exists(String filename)

    /**
     * Stores a regular file for holder
     * @param file
     * @param filename
     * @return actual filename
     */
    String store(File file, String filename)

    String store(File file)

    /**
     * Stores a file for multipart request
     * @param file
     * @param filename
     * @return actual filename
     */
    String store(MultipartFile file, String filename)

    String store(MultipartFile file)

    long getSize(String filename)
}