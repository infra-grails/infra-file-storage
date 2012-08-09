package ru.mirari.infra.file;

import groovy.util.ConfigObject;
import org.apache.commons.io.FileUtils;
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author alari
 * @since 11/16/11 12:19 PM
 */
public class LocalFileStorage extends FileStoragePrototype {

    String defaultBucket = "storage";
    String localRoot = "./web-app/f/";
    String urlRoot;

    @Autowired
    LocalFileStorage(GrailsApplication grailsApplication) {
        ConfigObject config = (ConfigObject) grailsApplication.getConfig().get("mirari");
        config = (ConfigObject) config.get("infra");
        config = (ConfigObject) config.get("file");
        Map localConf = ((ConfigObject) config.get("local")).flatten();

        localRoot = localConf.get("localRoot").toString().isEmpty() ? localRoot : localConf.get("localRoot").toString();
        defaultBucket = localConf.get("defaultBucket").toString();
        urlRoot = localConf.get("urlRoot").toString();
        if (urlRoot == null || urlRoot.isEmpty()) {
            urlRoot = ((Map) grailsApplication.getConfig().get("grails")).get("serverURL").toString().concat("f/");
        }
        if (!urlRoot.endsWith("/")) {
            urlRoot = urlRoot.concat("/");
        }
    }

    public void store(final File file, String path, String filename, String bucket) throws IOException {
        createDir(path, bucket);
        File newFile = new File(getFullLocalPath(path, filename.isEmpty() ? file.getName() : filename,
                bucket));

        FileUtils.copyFile(file, newFile);
    }

    public void delete(String path, String filename, String bucket) {
        new File(getFullLocalPath(path, filename, bucket)).delete();
    }

    public String getUrl(String path, String filename, String bucket) {
        return urlRoot.concat(getFullPath(path, filename, bucket));
    }

    private String getFullLocalPath(String path, String filename, String bucket) {
        return localRoot.concat(getFullPath(path, filename, bucket));
    }

    private String getFullPath(String path, String filename, String bucket) {
        String fullPath = bucket == null || bucket.isEmpty() ? defaultBucket : bucket;

        if (!fullPath.endsWith("/")) fullPath = fullPath.concat("/");
        fullPath = fullPath.concat(path);

        if (!fullPath.endsWith("/")) fullPath = fullPath.concat("/");
        fullPath = fullPath.concat(filename);

        return fullPath;
    }

    private void createDir(String path, String bucket) {
        new File(
                localRoot.concat(bucket == null || bucket.isEmpty() ? defaultBucket : bucket).concat("/").concat(path)
        ).mkdirs();
    }
}
