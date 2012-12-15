package ru.mirari.infra.file

import org.springframework.web.multipart.MultipartFile

/**
 * @author alari
 * @since 12/16/12 1:02 AM
 */
class FilesHolderHelper {
    void setFileNames(FilesHolder holder, def domain, List<String> fileNames) {
        domain."${holder.filesProperty()}" = fileNames
    }

    List<String> getFileNames(FilesHolder holder, final def domain) {
        domain."${holder.filesProperty()}"
    }

    String getPath(FilesHolder holder, final def domain) {
        Closure<String> path = (Closure<String>) holder.path().newInstance(domain, domain)
        path.call(domain)
    }

    String getBucket(FilesHolder holder, final def domain) {
        Closure<String> bucket = (Closure<String>) holder.bucket().newInstance(domain, domain)
        bucket.call(domain)
    }

    void checkFile(final FilesHolder holder, final File file) {
        String extension = file.name.substring(file.name.lastIndexOf(".") + 1)
        if (holder.allowedExtensions() && !(extension in holder.allowedExtensions())) {
            throw new IllegalArgumentException("Wrong file extension")
        }
    }

    void checkFile(final FilesHolder holder, final MultipartFile file) {
        String extension = file.originalFilename.substring(file.originalFilename.lastIndexOf(".") + 1)
        if (holder.allowedExtensions() && !(extension in holder.allowedExtensions())) {
            throw new IllegalArgumentException("Wrong file extension")
        }
    }

    FilesHolder getHolder(final def holder) {
        FilesHolder filesHolder = holder.class.getAnnotation(FilesHolder)
        if (!filesHolder) {
            throw new IllegalArgumentException("You have to provide a files holder with @FilesHolder annotation")
        }
        filesHolder
    }
}
