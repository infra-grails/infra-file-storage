Grails plugin: infra-file-storage
====================

Abstracts file storing infrastructure. Allows to change storage when deploying/testing an app, caching stored files stats, and so on.

Currently bundled storages:
- Local file system
- Amazon S3

Use the plugin, post your feedback!

Current release: _0.2-SNAPSHOT_

Installation
---------------------

Add to your `BuildConfig.groovy`:

```groovy
grails.project.dependency.resolution = {
   ...
   repositories {
           ...
           mavenRepo "http://mvn.quonb.org/repo"
           grailsRepo "http://mvn.quonb.org/repo", "quonb"
           ...
   }
   plugins {
           compile 'infra-file-storage:0.2-SNAPSHOT'
           ...
   }
```

Configuration
---------------------

Use `Config.groovy` to change the defaults:

```groovy
plugin{
    infraFileStorage {
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
                    buckets { // optional
                        bucketName = "http://customUrlRoot"
                    }
                }
                defaultStorageName = "local" // a storage name to use
            }
        }
    }
}
```

Usage
---------------------

Take a look on [Spock Integration tests](https://github.com/alari/infra-file-storage/tree/master/test/integration/infra/file/storage)

#### Basic files manager

```groovy
def holder = fileStorageService.getManager(path, bucket, false)
```

This method will return you a manager instance to manage files in a `bucked` and `path`, without caching files stats in database.
You may take a look on available methods there: [FilesManager](https://github.com/alari/infra-file-storage/tree/master/src/groovy/infra/file/storage/FilesManager.groovy)

#### Annotated files manager

A more sophisticated way to manage files is to annotate its holder (e.g. domain object) with [FilesHolder](https://github.com/alari/infra-file-storage/tree/master/src/groovy/infra/file/storage/FilesHolder.groovy) annotation

If you set `enableFileDomains` option of an annotation to `true` (default is `true`), file stats will be stored in database. To change the stats persisting strategy, take a look there: [beans to override](https://github.com/alari/infra-file-storage/tree/master/src/groovy/infra/file/storage/domain/).



Companion plugins
-----------------------

[Images Storing & Resizing](https://github.com/alari/infra-images)
