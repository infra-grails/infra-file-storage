package ru.mirari.infra

import ru.mirari.infra.file.FileStorage

class FileStorageService implements FileStorage{

    static transactional = false

    @Delegate FileStorage fileStorage
}
