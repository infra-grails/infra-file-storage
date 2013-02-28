package infra.file.storage.domain

import infra.file.storage.FileDomain

/**
 * @author alari
 * @since 2/18/13 6:35 PM
 */
class FileDomainRepoImpl implements FileDomainRepo {
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
                bucket: bucket,
        )
    }

    @Override
    FileInfoDomain update(String filename, long size) {
        FileDomain heldFile = (FileDomain) getDomain(filename)
        if (!heldFile) {
            heldFile = new FileDomain(
                    path: path,
                    storageName: storageName,
                    bucket: bucket,
                    filename: filename,
                    size: size
            )
            heldFile.save(failOnError: true, flush: true)
        } else if (heldFile.size != size) {
            heldFile.size = size
            heldFile.save(failOnError: true)
        }
        assert heldFile.id
    }

    @Override
    FileInfoDomain getDomain(String filename) {
        FileDomain.findWhere(path: path,
                storageName: storageName,
                bucket: bucket,
                filename: filename)
    }

    void delete(String filename) {
        getDomain(filename)?.delete()
    }

    void delete() {
        FileDomain.deleteAll(FileDomain.findAllWhere(path: path, bucket: bucket, storageName: storageName))
    }
}
