package ru.mirari.file

/**
 * @author alari
 * @since 11/1/11 1:43 PM
 */
class FileStorageHolder implements FileStorage {
    @Delegate FileStorage storage
}
