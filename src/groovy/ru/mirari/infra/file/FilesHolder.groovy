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
    /**
     * A closure<String> that returns a bucket name associated with current holder domain
     * @return
     */
    Class bucket() default {}

    /**
     * A Closure<String> that returns unique holder identifier
     * @return
     */
    Class path()

    /**
     * The name of storage to store to
     * @return
     */
    Class storage() default {}

    /**
     * What List<String> property of an annotated domain object to read and write file names to
     * @return
     */
    String filesProperty() default "fileNames"

    /**
     * List of allowed files extensions -- empty to no restrictions
     * @return
     */
    String[] allowedExtensions() default []
}