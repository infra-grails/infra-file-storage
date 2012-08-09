package ru.mirari.infra.file

import eu.medsea.mimeutil.MimeType
import eu.medsea.mimeutil.MimeUtil

/**
 * @author alari
 * @since 2/4/12 7:46 PM
 */
class FileInfo {
    private final File file
    private final MimeType mimeType
    private final String originalFilename

    static {
        MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
    }

    FileInfo(final File file) {
        this.file = file
        this.originalFilename = file.name
        mimeType = MimeUtil.getMostSpecificMimeType(MimeUtil.getMimeTypes(file))
    }

    FileInfo(final File file, String originalFilename) {
        this.file = file
        this.originalFilename = originalFilename
        mimeType = MimeUtil.getMostSpecificMimeType(MimeUtil.getMimeTypes(file))
    }

    String getMediaType() {
        mimeType.mediaType
    }

    String getSubType() {
        mimeType.subType
    }

    String getExtension() {
        originalFilename.contains(".") ? originalFilename.substring(originalFilename.lastIndexOf(".") + 1) : originalFilename
    }

    String getTitle() {
        originalFilename.contains(".") ? originalFilename.substring(0, originalFilename.lastIndexOf(".")) : originalFilename
    }

    File getFile() {
        file
    }
}
