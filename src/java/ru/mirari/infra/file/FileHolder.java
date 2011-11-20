package ru.mirari.infra.file;

import java.util.List;

/**
 * @author alari
 * @since 11/16/11 12:06 PM
 */
public interface FileHolder {
    String getFilesPath();

    String getFilesBucket();

    List<String> getFileNames();
}
