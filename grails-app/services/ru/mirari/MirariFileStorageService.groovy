package ru.mirari

import ru.mirari.file.FileStorage

class MirariFileStorageService implements FileStorage{

    static transactional = false

    @Delegate FileStorage fileStorage
}
