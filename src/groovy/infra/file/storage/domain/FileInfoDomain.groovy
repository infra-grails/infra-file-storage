package infra.file.storage.domain

/**
 * @author alari
 * @since 2/18/13 6:33 PM
 */
interface FileInfoDomain {
    String getFilename()

    Long getSize()

    void delete()
}
