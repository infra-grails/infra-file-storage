package infra.file.storage.domain

/**
 * @author alari
 * @since 2/18/13 6:49 PM
 */
public interface FileDomainRepoProvider {
    FileDomainRepo get(String path, String storageName, String bucket)
}