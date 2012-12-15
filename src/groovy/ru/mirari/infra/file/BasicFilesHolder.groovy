package ru.mirari.infra.file

/**
 * @author alari
 * @since 12/16/12 1:00 AM
 */
@FilesHolder(
path = { path },
bucket = { bucket }
)
class BasicFilesHolder {
    String path
    String bucket
    List<String> fileNames
}
