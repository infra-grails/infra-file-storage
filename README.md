Grails plugin: mirari-fileStorage
====================

Stores files on Amazon S3 when deployed, on localhost when testing/developing.

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