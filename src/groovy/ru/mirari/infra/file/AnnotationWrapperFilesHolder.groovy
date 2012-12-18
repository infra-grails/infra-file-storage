package ru.mirari.infra.file

import ru.mirari.infra.FileStorageService

/**
 * @author alari
 * @since 12/18/12 6:34 PM
 */
class AnnotationWrapperFilesHolder extends AbstractFilesHolder {
    private def domain
    private final FilesHolder holder
    private final String propertyName
    private final FileStorage storage

    AnnotationWrapperFilesHolder(def domain, FileStorage storage) {
        this.domain = domain
        holder = domain.class.getAnnotation(FilesHolder)
        propertyName = holder.filesProperty()
        this.storage = storage
    }

    AnnotationWrapperFilesHolder(def domain, FileStorageService fileStorageService) {
        this.domain = domain
        holder = domain.class.getAnnotation(FilesHolder)
        propertyName = holder.filesProperty()
        this.storage = storage
        final String storageName = ((Closure<String>) holder.storage().newInstance(domain, domain)).call()
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
