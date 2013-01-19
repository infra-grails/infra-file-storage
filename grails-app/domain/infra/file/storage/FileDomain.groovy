package infra.file.storage

class FileDomain {
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
