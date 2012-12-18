package ru.mirari.infra.file;

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
    public String store(final MultipartFile file, String path, String filename, String bucket) throws Exception;

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
    public String store(final File file, String path, String filename, String bucket) throws Exception;

    /**
     * Deletes a file
     *
     * @param path
     * @param filename
     * @param bucket
     * @throws Exception
     */
    public void delete(String path, String filename, String bucket) throws Exception;

    /**
     * Checks if a file really exists in the storage
     *
     * @param path
     * @param filename
     * @param bucket
     * @return
     * @throws Exception
     */
    public boolean exists(String path, String filename, String bucket) throws Exception;

    /**
     * Returns an accessible absolute url for a file
     *
     * @param path
     * @param filename
     * @param bucket
     * @return
     */
    public String getUrl(String path, String filename, String bucket);
}
