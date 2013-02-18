package infra.file.storage.domain

/**
 * @author alari
 * @since 2/18/13 6:33 PM
 */
public interface FileDomainRepo {
    Collection<FileInfoDomain> list()
    FileInfoDomain update(String filename, long size)
    FileInfoDomain getDomain(String filename)
    void delete(String filename)
    void delete()
}