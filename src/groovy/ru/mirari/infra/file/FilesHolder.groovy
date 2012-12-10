package ru.mirari.infra.file

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * @author alari
 * @since 12/11/12 12:48 AM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FilesHolder {
    String bucket() default ""
    Class path()

    Class fileNames() default {it.fileNames}
    Class setFileNames() default {domain, fileNames -> domain.fileNames = fileNames}

    String[] allowedExtensions() default [""]
}