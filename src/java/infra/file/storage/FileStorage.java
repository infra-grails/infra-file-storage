package infra.file.storage;

import infra.file.storage.ex.StorageException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @author alari
 * @since 11/16/11 12:07 PM
 */
public interface FileStorage {
    /**
     * Name of file storage as it's used in config
     *
     * @return
     */
    public String getName();

    /**
     * Stores a multipart file, identified by path and filename, in a bucket storage
     *
     * @param file
     * @param path
     * @param filename
     * @param bucket
     * @return actual stored filename
     * @throws Exception
     */
    public String store(final MultipartFile file, String path, String filename, String bucket) throws StorageException;

    /**
     * Stores a file, identified by path and filename, in a bucket storage
     *
     * @param file
     * @param path
     * @param filename
     * @param bucket
     * @return actual stored filename
     * @throws Exception
     */
    public String store(final File file, String path, String filename, String bucket) throws StorageException;

    /**
     * Deletes a file
     *
     * @param path
     * @param filename
     * @param bucket
     * @throws Exception
     */
    public void delete(String path, String filename, String bucket) throws StorageException;

    /**
     * Checks if a file really exists in the storage
     *
     * @param path
     * @param filename
     * @param bucket
     * @return
     * @throws Exception
     */
    public boolean exists(String path, String filename, String bucket) throws StorageException;

    /**
     * Returns an accessible absolute url for a file
     *
     * @param path
     * @param filename
     * @param bucket
     * @return
     */
    public String getUrl(String path, String filename, String bucket);

    /**
     * Returns size of a stored file
     *
     * @param path
     * @param filename
     * @param bucket
     * @return
     */
    public long getSize(String path, String filename, String bucket) throws StorageException;

    /**
     * Returns file, downloads it if necessary
     *
     * @param path
     * @param filename
     * @param bucket
     * @return
     */
    public File getFile(String path, String filename, String bucket) throws StorageException;
}
