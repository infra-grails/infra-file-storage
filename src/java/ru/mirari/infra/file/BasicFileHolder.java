package ru.mirari.infra.file;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author alari
 * @since 11/18/12 10:51 PM
 */
public class BasicFileHolder implements FileHolder {
    private String id;

    private String filesBucket = null;
    private List<String> fileNames = new LinkedList<String>();
    private String filesPath = null;

    public BasicFileHolder(String id) {
        this.id = id;
        init();
    }

    public BasicFileHolder(String id, String filesBucket) {
        this.filesBucket = filesBucket;
        this.id = id;
        init();
    }

    private void init() {
        if(id == null || id.isEmpty()) {
            throw new IllegalArgumentException("You must provide a valid id to use BasicFileHolder");
        }
        if(filesBucket == null) {
            filesBucket = "";
        }
        filesPath = "f/".concat(id);
    }

    @Override
    public String getFilesPath() {
        return filesPath;
    }

    @Override
    public String getFilesBucket() {
        return filesBucket;
    }

    @Override
    public List<String> getFileNames() {
        return fileNames;
    }

    @Override
    public void setFileNames(Collection<String> names) {
        fileNames.clear();
        fileNames.addAll(names);
    }
}
