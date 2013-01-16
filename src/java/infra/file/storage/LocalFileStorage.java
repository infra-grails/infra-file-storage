package infra.file.storage;

import groovy.util.ConfigObject;
import org.apache.commons.io.FileUtils;
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author alari
 * @since 11/16/11 12:19 PM
 */
@Component
public class LocalFileStorage extends FileStoragePrototype {

    String defaultBucket = "storage";
    String localRoot = "./web-app/f/";
    String urlRoot;

    @Autowired
    LocalFileStorage(GrailsApplication grailsApplication) {
        Map localConf;
        ConfigObject config;
        try {
            config = (ConfigObject) grailsApplication.getConfig().get("plugin");
            config = (ConfigObject) config.get("infraFileStorage");
            localConf = ((ConfigObject) config.get("local")).flatten();
        } catch (NullPointerException npe) {
            urlRoot = ((Map) grailsApplication.getConfig().get("grails")).get("serverURL").toString().concat("/f/");
            if (urlRoot.equals("{}/f/")) urlRoot = "/f/";
            return;
        }

        localRoot = localConf.get("localRoot").toString().isEmpty() ? localRoot : localConf.get("localRoot").toString();
        defaultBucket = localConf.get("defaultBucket").toString();

        urlRoot = localConf.get("urlRoot").toString();
        if (urlRoot == null || urlRoot.isEmpty()) {
            urlRoot = ((Map) grailsApplication.getConfig().get("grails")).get("serverURL").toString();
            if (urlRoot.equals("{}")) urlRoot = "/";
            if (!urlRoot.endsWith("/")) urlRoot = urlRoot.concat("/");
            urlRoot = urlRoot.concat("f/");
        }
        if (!urlRoot.endsWith("/")) {
            urlRoot = urlRoot.concat("/");
        }
    }

    @Override
    public String store(final File file, String path, String filename, String bucket) throws IOException {
        createDir(path, bucket);
        File newFile = new File(getFullLocalPath(path, (filename == null || filename.isEmpty()) ? file.getName() : filename,
                bucket));
        FileUtils.copyFile(file, newFile);

        return newFile.getName();
    }

    @Override
    public void delete(String path, String filename, String bucket) {
        new File(getFullLocalPath(path, filename, bucket)).delete();
    }

    @Override
    public boolean exists(String path, String filename, String bucket) throws Exception {
        return new File(getFullLocalPath(path, filename, bucket)).isFile();
    }

    @Override
    public String getUrl(String path, String filename, String bucket) {
        return urlRoot.concat(getFullPath(path, filename, bucket));
    }

    @Override
    public long getSize(String path, String filename, String bucket) {
        return new File(getFullLocalPath(path, filename, bucket)).length();
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
