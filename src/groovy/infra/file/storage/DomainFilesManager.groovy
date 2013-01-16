package infra.file.storage

import org.springframework.web.multipart.MultipartFile

/**
 * @author alari
 * @since 1/15/13 9:45 PM
 */
class DomainFilesManager implements FilesManager {
    private final FilesManager holder

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
    }

    @Override
    void delete() {
        holder.delete()
        FileDomain.deleteAll(FileDomain.findAllWhere(path: path, bucket: bucket, storageName: storage.name))
    }

    @Override
    long getSize(String filename) {
        getDomain(filename)?.size
    }

    private touchHeldFile(String filename, long size) {
        FileDomain heldFile = FileDomain.findOrCreateWhere(
                filename: filename,
                path: path,
                bucket: bucket,
                storageName: storage.name
        )
        println heldFile
        if (heldFile.size != size) {
            heldFile.size = size
            heldFile.save()
        }
        println heldFile
        assert heldFile.id
    }

    FileDomain getDomain(String filename) {
        FileDomain.findWhere(filename: filename, path: path, bucket: bucket, storageName: storage.name)
    }

    /**
     * DELEGATING
     * @return
     */

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
        holder.exists(filename)
    }
}
