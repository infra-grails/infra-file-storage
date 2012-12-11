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

    String filesProperty() default "fileNames"

    String[] allowedExtensions() default [""]
}