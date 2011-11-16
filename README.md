Grails plugin: mirari-fileStorage
====================

Stores files on Amazon S3 when deployed, on localhost when testing/developing.

Installation
---------------------

Clone plugin sources (you may fork it at first, and that'll be great) and set sources directory in `BuildConfig
.groovy`:

    grails.plugin.location.'mirari-fileStorage' = "../mirari-fileStorage"

You will need valid S3 credentials to deploy the plugin.

And notice: you shouldn't call `grails install-plugin` or add direct dependency to `BuildConfig.groovy`!

Otherwise, you may download packaged version of the plugin and call

`grails install-plugin grails-mirari-file-storage.0.1.zip`

Usage
---------------------

At first, after installation, you should assign your Config.groovy:

    grails {
        mirari {
            fileStorage {
                local {
                    localRoot = "./web-app/" // (optional) where to store local files
                    defaultBucket = "storage" // (optional) will appear as a folder in your localRoot
                    urlRoot = "/mirari/" // (optional) Set absolute path to localRoot
                }
                s3 {
                    defaultBucket = "(required) name of your Amazon S3 Bucket to be used for default"
                    accessKey = "ACCESS KEY"
                    secretKey = "SECRET KEY"
                    urlRoot = "http://statics.example.com/" // (optional) This will be used to make URLs for your files
                }
            }
        }
    }

Then just use `MirariFileStorageService` or `fileStorage` Spring bean to store your files.

How to remove local storage from WAR
---------------------

Add to your `BuildConfig.groovy`:

    grails.war.resources = { stagingDir, args ->
        delete(dir: "${stagingDir}/storage") // Replace the path with the pathes for all your local buckets
    }