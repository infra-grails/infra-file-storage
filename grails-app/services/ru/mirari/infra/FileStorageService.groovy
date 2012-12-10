package ru.mirari.infra

import ru.mirari.infra.file.FileStorage
import ru.mirari.infra.file.FilesHolder

class FileStorageService implements FileStorage {

    static transactional = false

    @Delegate FileStorage fileStorage

    void store(def holder, final File file, String filename = null) {
        FilesHolder filesHolder = getHolder(holder)

        if (!filename) filename = file.name

        checkFile(filesHolder, file)

        fileStorage.store(file, getPath(filesHolder, holder), filename ?: "", filesHolder.bucket())

        List<String> fileNames = getFileNames(filesHolder, holder) ?: []
        if (!fileNames.contains(filename)) fileNames.add(filename)

        setFileNames(filesHolder, holder, fileNames)
    }

    void delete(def domain) {
        FilesHolder holder = getHolder(domain)
        List<String> fileNames = getFileNames(holder, domain)
        if (fileNames) {
            for(String filename : fileNames) {
                fileStorage.delete(getPath(holder, domain), filename, holder.bucket())
            }
        }
        setFileNames(holder, domain, [])
    }

    void delete(def domain, String filename) {
        FilesHolder holder = getHolder(domain)
        List<String> fileNames = getFileNames(holder, domain)

        if (filename in fileNames) {
            fileStorage.delete(getPath(holder, domain), filename, holder.bucket())
        }
        fileNames.remove(filename)
        setFileNames(holder, domain, fileNames)
    }

    String getUrl(def domain, String filename=null) {
        FilesHolder holder = getHolder(domain)
        if (!filename) {
            List<String> fileNames = getFileNames(holder, domain)
            if (fileNames.size() == 1) filename = fileNames.first()
        }
        if (!filename) {
            throw new IllegalArgumentException("Null filename is allowed only when domain (already) holds only a single file")
        }
        fileStorage.getUrl(getPath(holder, domain), filename, holder.bucket())
    }

    private void setFileNames(FilesHolder holder, def domain, List<String> fileNames) {
        holder.setFileNames().newInstance(domain, domain).call(domain, fileNames)
    }

    private List<String> getFileNames(FilesHolder holder, final def domain) {
        holder.fileNames().newInstance(domain, domain).call(domain)
    }

    private String getPath(FilesHolder holder, final def domain) {
        Closure<String> path = holder.path().newInstance(domain, domain)
        path.call(domain)
    }

    private checkFile(final FilesHolder holder, final File file) {
        String extension = file.name.substring(file.name.lastIndexOf(".")+1)
        if (holder.allowedExtensions() && !(extension in holder.allowedExtensions())) {
            throw new IllegalArgumentException("Wrong file extension")
        }
    }

    private FilesHolder getHolder(final def holder) {
        FilesHolder filesHolder = holder.class.getAnnotation(FilesHolder)
        if (!filesHolder) {
            throw new IllegalArgumentException("You have to provide a files holder with @FilesHolder annotation")
        }
        filesHolder
    }
}
