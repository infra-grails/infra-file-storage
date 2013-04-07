package infra.file.storage.domain

import groovy.transform.CompileStatic
import infra.file.storage.FileStorage
import infra.file.storage.FilesManager
import org.springframework.web.multipart.MultipartFile

/**
 * @author alari
 * @since 1/15/13 9:45 PM
 */
@CompileStatic
class DomainFilesManager implements FilesManager {
    private final FilesManager holder

    final private FileDomainRepo fileDomainRepo

    DomainFilesManager(FilesManager holder, FileDomainRepoProvider repoProvider) {
        this.holder = holder
        fileDomainRepo = repoProvider.get(getPath(), getStorage().name, getBucket())
        refresh()
    }

    void refresh() {
        setFileNames fileDomainRepo.list().collect { FileInfoDomain d -> d.filename }
    }

    @Override
    String store(File file, String filename = null) {
        filename = holder.store(file, filename)
        fileDomainRepo.update(filename, file.length())
        if (!getFileNames().contains(filename)) getFileNames().add(filename)
        filename
    }

    @Override
    String store(MultipartFile file, String filename = null) {
        filename = holder.store(file, filename)
        fileDomainRepo.update(filename, file.size)
        if (!getFileNames().contains(filename)) getFileNames().add(filename)
        filename
    }

    @Override
    void delete(String filename) {
        holder.delete(filename)
        fileDomainRepo.delete(filename)
        getFileNames().remove(filename)
    }

    @Override
    void delete() {
        holder.delete()
        fileDomainRepo.delete()
        setFileNames([])
    }

    @Override
    long getSize(String filename) {
        getDomain(filename)?.size
    }

    FileInfoDomain getDomain(String filename) {
        fileDomainRepo.getDomain(filename)
    }

    /**
     * DELEGATING
     * @return
     */

    @Override
    File getFile() {
        holder.getFile()
    }

    @Override
    File getFile(String filename) {
        holder.getFile(filename)
    }

    @Override
    FileStorage getStorage() {
        holder.storage
    }

    @Override
    String getPath() {
        holder.path
    }

    @Override
    String getBucket() {
        holder.bucket
    }

    @Override
    Collection<String> getFileNames() {
        holder.fileNames
    }

    @Override
    void setFileNames(Collection<String> fileNames) {
        holder.fileNames = fileNames
    }

    @Override
    String[] getAllowedExtensions() {
        holder.allowedExtensions
    }

    @Override
    String getUrl(String filename) {
        holder.getUrl(filename)
    }

    @Override
    String getUrl() {
        holder.getUrl()
    }

    @Override
    boolean exists(String filename) {
        getDomain(filename) != null
    }
}
