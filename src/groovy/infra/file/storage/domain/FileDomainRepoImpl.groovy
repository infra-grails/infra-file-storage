package infra.file.storage.domain

import infra.file.storage.FileDomain

/**
 * @author alari
 * @since 2/18/13 6:35 PM
 */
class FileDomainRepoImpl implements FileDomainRepo {
    private Map<String,FileDomain> fileDomainMap = [:]

    private final String path
    private final String storageName
    private final String bucket

    FileDomainRepoImpl(String path, String storageName, String bucket) {
        this.path = path
        this.storageName = storageName
        this.bucket = bucket
    }

    @Override
    Collection<FileInfoDomain> list() {
        FileDomain.findAllWhere(
                path: path,
                storageName: storageName,
                bucket: bucket
        )
    }

    @Override
    FileInfoDomain update(String filename, long size) {
        FileDomain heldFile = (FileDomain)FileDomain.findOrSaveWhere(
                filename: filename,
                path: path,
                bucket: bucket,
                storageName: storageName
        )
        if (heldFile.size != size) {
            heldFile.size = size
            heldFile.save()
        }
        assert heldFile.id
        fileDomainMap.put(filename, heldFile)
    }

    @Override
    FileInfoDomain getDomain(String filename) {
        if (!fileDomainMap.containsKey(filename)) {
            fileDomainMap.put filename, (FileDomain)FileDomain.findWhere(filename: filename, path: path, bucket: bucket, storageName: storageName)
        }
        fileDomainMap.get(filename)
    }

    void delete(String filename){
        getDomain(filename)?.delete()
        fileDomainMap.remove(filename)
    }

    void delete() {
        FileDomain.deleteAll(FileDomain.findAllWhere(path: path, bucket: bucket, storageName: storageName))
        fileDomainMap.clear()
    }
}
