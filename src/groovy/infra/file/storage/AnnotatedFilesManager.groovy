package infra.file.storage
/**
 * @author alari
 * @since 12/18/12 6:34 PM
 */
class AnnotatedFilesManager extends AbstractFilesManager {
    private Object domain
    private final FilesHolder holder
    private final String propertyName
    private final FileStorage storage


    AnnotatedFilesManager(def domain, FileStorageService fileStorageService, FilesHolder holder) {
        this.domain = domain
        this.holder = holder instanceof FilesHolder ? holder : (FilesHolder)domain.class.getAnnotation(FilesHolder)

        propertyName = this.holder.filesProperty()

        final String storageName = ((Closure<String>) this.holder.storage().newInstance(domain, domain)).call()
        storage = fileStorageService.getFileStorage(storageName)
    }

    @Override
    FileStorage getStorage() {
        storage
    }

    @Override
    String getPath() {
        ((Closure<String>) holder.path().newInstance(domain, domain)).call()
    }

    @Override
    String getBucket() {
        ((Closure<String>) holder.bucket().newInstance(domain, domain)).call()
    }

    @Override
    Collection<String> getFileNames() {
        if(!domain.hasProperty(propertyName)) return []

        if (!domain."${propertyName}") {
            domain."${propertyName}" = []
        }

        domain."${propertyName}"
    }

    @Override
    void setFileNames(Collection<String> fileNames) {
        if (domain.hasProperty(propertyName)) {
            domain."${propertyName}" = fileNames
        }
    }

    @Override
    String[] getAllowedExtensions() {
        holder.allowedExtensions()
    }
}
