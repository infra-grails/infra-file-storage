package infra.file.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author alari
 * @since 12/12/12 12:50 AM
 */
abstract public class FileStoragePrototype implements FileStorage {
    private volatile String name = null;

    @Override
    public String getName() {
        if (name == null) {
            synchronized (this) {
                if (name == null) {
                    name = this.getClass().getSimpleName();
                    name = name.substring(0, 1).toLowerCase().concat(name.substring(1, name.length() - FileStorage.class.getSimpleName().length()));
                }
            }
        }
        return name;
    }

    @Override
    public String store(final MultipartFile file, String path, String filename, String bucket) throws Exception {
        if (filename == null || filename.isEmpty()) filename = file.getOriginalFilename();
        File tmp = File.createTempFile("uploaded", filename);
        file.transferTo(tmp);
        String storedFilename = store(tmp, path, filename, bucket);
        tmp.delete();
        return storedFilename;
    }

    @Override
    public long getSize(String path, String filename, String bucket) throws Exception {
        HttpURLConnection con =
                (HttpURLConnection) new URL(getUrl(path, filename, bucket)).openConnection();
        con.setRequestMethod("HEAD");
        return con.getContentLengthLong();
    }
}
