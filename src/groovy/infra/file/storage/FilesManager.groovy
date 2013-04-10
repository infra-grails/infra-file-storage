package infra.file.storage

import org.springframework.web.multipart.MultipartFile

/**
 * @author alari
 * @since 1/15/13 9:58 PM
 */
public interface FilesManager {
    /**
     * Returns a file object for an only file stored with this manager, downloads it if necessary
     *
     * @return
     */
    File getFile()

    /**
     * Returns a file object for given filename, downloads it at first if necessary
     * @param filename
     * @return
     */
    File getFile(String filename)

    /**
     * Returns an actual FileStorage for this manager
     * @return
     */
    public FileStorage getStorage()

    /**
     * Returns the path for current manager
     *
     * @return
     */
    public String getPath()

    /**
     * Returns the bucket name
     *
     * @return
     */
    public String getBucket()

    /**
     * Returns stored files names
     *
     * @return
     */
    public Collection<String> getFileNames()

    /**
     * Sets file names for manager -- probably given by the cache
     * @param fileNames
     */
    public void setFileNames(Collection<String> fileNames)

    /**
     * Returns an array of extensions allowed to store files with this manager
     *
     * @return array of extensions
     */
    public String[] getAllowedExtensions()

    /**
     * Returns an accessible URL for filename
     *
     * @param filename
     * @return
     */
    String getUrl(String filename)

    /**
     * Returns an accessible URL of a single file in a manager
     *
     * @return
     */
    String getUrl()

    /**
     * Deletes a file
     *
     * @param filename
     */
    void delete(String filename)

    /**
     * Deletes all the files managed with current manager
     */
    void delete()

    /**
     * Checks if a file exists
     *
     * @param filename
     * @return
     */
    boolean exists(String filename)

    /**
     * Stores a regular file for holder
     *
     * @param file
     * @param filename
     * @return actual filename
     */
    String store(File file, String filename)

    /**
     * Stores a file with the given name
     *
     * @param file
     * @return actual filename
     */
    String store(File file)

    /**
     * Stores a file for multipart request
     * @param file
     * @param filename
     * @return actual filename
     */
    String store(MultipartFile file, String filename)

    /**
     * Stores a file for multipart request, uses given name
     *
     * @param file
     * @return actual filename
     */
    String store(MultipartFile file)

    /**
     * Returns size of a file in bytes
     *
     * @param filename
     * @return file size in bytes
     */
    long getSize(String filename)
}