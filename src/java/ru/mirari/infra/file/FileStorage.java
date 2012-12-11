package ru.mirari.infra.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @author alari
 * @since 11/16/11 12:07 PM
 */
public interface FileStorage {
    public void store(final MultipartFile file, String path, String filename, String bucket) throws Exception;

    public void store(final File file, String path, String filename, String bucket) throws Exception;

    public void delete(String path, String filename, String bucket) throws Exception;

    public boolean exists(String path, String filename, String bucket) throws Exception;

    public String getUrl(String path, String filename, String bucket);
}
