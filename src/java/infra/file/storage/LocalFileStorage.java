package infra.file.storage;

import infra.file.storage.config.LocalStorageConfig;
import org.apache.commons.io.FileUtils;
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * @author alari
 * @since 11/16/11 12:19 PM
 */
@Component
public class LocalFileStorage extends FileStoragePrototype {

    private LocalStorageConfig config;

    @Autowired
    LocalFileStorage(GrailsApplication grailsApplication) {
        config = new LocalStorageConfig(grailsApplication);
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
        return config.getUrlRoot().concat(getFullPath(path, filename, bucket));
    }

    @Override
    public long getSize(String path, String filename, String bucket) {
        return new File(getFullLocalPath(path, filename, bucket)).length();
    }

    @Override
    public File getFile(String path, String filename, String bucket) {
        return new File(getFullLocalPath(path, filename, bucket));
    }

    private String getFullLocalPath(String path, String filename, String bucket) {
        return config.getLocalRoot().concat(getFullPath(path, filename, bucket));
    }

    private String getFullPath(String path, String filename, String bucket) {
        String fullPath = bucket == null || bucket.isEmpty() ? config.getDefaultBucket() : bucket;

        if (!fullPath.endsWith("/")) fullPath = fullPath.concat("/");
        fullPath = fullPath.concat(path);

        if (!fullPath.endsWith("/")) fullPath = fullPath.concat("/");
        fullPath = fullPath.concat(filename);
        return fullPath;
    }

    private void createDir(String path, String bucket) {
        new File(
                config.getLocalRoot().concat(bucket == null || bucket.isEmpty() ? config.getDefaultBucket() : bucket).concat("/").concat(path)
        ).mkdirs();
    }
}
