package ru.mirari.infra.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @author alari
 * @since 12/12/12 12:50 AM
 */
abstract public class FileStoragePrototype implements FileStorage {
    @Override
    public void store(final MultipartFile file, String path, String filename, String bucket) throws Exception {
        if (filename == null || filename.isEmpty()) filename = file.getOriginalFilename();
        File tmp = File.createTempFile("uploaded", filename);
        file.transferTo(tmp);
        store(tmp, path, filename, bucket);
    }
}
