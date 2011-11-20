package ru.mirari.infra.file;

import org.jets3t.service.ServiceException;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * @author alari
 * @since 11/16/11 12:07 PM
 */
public interface FileStorage {
    public void store(final File file, String path, String filename, String bucket) throws IOException, NoSuchAlgorithmException, ServiceException;

    public void store(final File file, final FileHolder holder, String filename) throws IOException, NoSuchAlgorithmException, ServiceException;

    public void delete(String path, String filename, String bucket) throws ServiceException;

    public void delete(final FileHolder holder, String filename) throws ServiceException;

    public void delete(final FileHolder holder) throws ServiceException;

    public String getUrl(String path, String filename, String bucket);

    public String getUrl(final FileHolder holder, String filename);
}
