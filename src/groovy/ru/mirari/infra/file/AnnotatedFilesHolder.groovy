package ru.mirari.infra.file

import ru.mirari.infra.FileStorageService

/**
 * @author alari
 * @since 12/18/12 6:34 PM
 */
class AnnotatedFilesHolder extends AbstractFilesHolder {
    private def domain
    private final FilesHolder holder
    private final String propertyName
    private final FileStorage storage


    AnnotatedFilesHolder(def domain, FileStorageService fileStorageService, FilesHolder holder) {
        this.domain = domain
        this.holder = holder instanceof FilesHolder ? holder : domain.class.getAnnotation(FilesHolder)

        propertyName = this.holder.filesProperty()

        final String storageName = ((Closure<String>) this.holder.storage().newInstance(domain, domain)).call()
        storage = fileStorageService.getFileStorage(storageName)
    }

    @Override
    FileStorage getStorage() {
        storage
    }

    @Override
    String getPath() {
        ((Closure<String>) holder.path().newInstance(domain, domain)).call()
    }

    @Override
    String getBucket() {
        ((Closure<String>) holder.bucket().newInstance(domain, domain)).call()
    }

    @Override
    Collection<String> getFileNames() {
        if (!domain."${propertyName}") {
            domain."${propertyName}" = []
        }
        domain."${propertyName}"
    }

    @Override
    void setFileNames(Collection<String> fileNames) {
        domain."${propertyName}" = fileNames
    }

    @Override
    String[] getAllowedExtensions() {
        holder.allowedExtensions()
    }
}
