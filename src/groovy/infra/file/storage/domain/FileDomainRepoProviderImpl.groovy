package infra.file.storage.domain

/**
 * @author alari
 * @since 2/18/13 6:59 PM
 */
class FileDomainRepoProviderImpl implements FileDomainRepoProvider {
    @Override
    FileDomainRepo get(String path, String storageName, String bucket) {
        new FileDomainRepoImpl(path, storageName, bucket)
    }
}
