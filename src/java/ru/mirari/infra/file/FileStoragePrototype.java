package ru.mirari.infra.file;

import org.jets3t.service.ServiceException;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * @author alari
 * @since 11/16/11 12:10 PM
 */
abstract public class FileStoragePrototype implements FileStorage {
    public void store(final File file, final FileHolder holder, String filename) throws IOException, NoSuchAlgorithmException, ServiceException {
        store(file, holder.getFilesPath(), filename, holder.getFilesBucket());
    }

    public void delete(final FileHolder holder, String filename) throws ServiceException {
        if(filename != null) delete(holder.getFilesPath(), filename, holder.getFilesBucket());
    }

    public void delete(final FileHolder holder) throws ServiceException {
        for (String fn : holder.getFileNames()) {
            if(fn != null) delete(holder, fn);
        }
    }

    public String getUrl(final FileHolder holder, String filename) {
        return getUrl(holder.getFilesPath(), filename, holder.getFilesBucket());
    }
}
