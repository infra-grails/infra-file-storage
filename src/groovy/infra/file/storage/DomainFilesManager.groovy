package infra.file.storage

import groovy.transform.CompileStatic
import org.springframework.web.multipart.MultipartFile

/**
 * @author alari
 * @since 1/15/13 9:45 PM
 */
@CompileStatic
class DomainFilesManager implements FilesManager {
    private final FilesManager holder
    private Map<String,FileDomain> fileDomainMap = [:]

    DomainFilesManager(FilesManager holder) {
        this.holder = holder
        fileNames = FileDomain.findAllWhere(
                path: path,
                storageName: storage.name,
                bucket: bucket
        )*.filename
    }

    @Override
    String store(File file, String filename = null) {
        filename = holder.store(file, filename)
        touchHeldFile(filename, file.length())
        filename
    }

    @Override
    String store(MultipartFile file, String filename = null) {
        filename = holder.store(file, filename)
        touchHeldFile(filename, file.size)
        filename
    }

    @Override
    void delete(String filename) {
        holder.delete(filename)
        getDomain(filename)?.delete()
        fileDomainMap.remove(filename)
    }

    @Override
    void delete() {
        holder.delete()
        FileDomain.deleteAll(FileDomain.findAllWhere(path: path, bucket: bucket, storageName: storage.name))
        fileDomainMap.clear()
    }

    @Override
    long getSize(String filename) {
        getDomain(filename)?.size
    }

    private touchHeldFile(String filename, long size) {
        FileDomain heldFile = (FileDomain)FileDomain.findOrSaveWhere(
                filename: filename,
                path: path,
                bucket: bucket,
                storageName: storage.name
        )
        if (heldFile.size != size) {
            heldFile.size = size
            heldFile.save()
        }
        assert heldFile.id
        fileDomainMap.put(filename, heldFile)
    }

    FileDomain getDomain(String filename) {
        if (!fileDomainMap.containsKey(filename)) {
            fileDomainMap.put filename, (FileDomain)FileDomain.findWhere(filename: filename, path: path, bucket: bucket, storageName: storage.name)
        }
        fileDomainMap.get(filename)
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
