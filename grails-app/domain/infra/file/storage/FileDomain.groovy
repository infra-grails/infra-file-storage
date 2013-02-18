package infra.file.storage

import infra.file.storage.domain.FileInfoDomain

class FileDomain implements FileInfoDomain {
    String path
    String filename
    String storageName
    String bucket

    Long size

    static constraints = {
        size nullable: true
        bucket nullable: true
        filename unique: ["path","storageName","bucket"]
    }
}
