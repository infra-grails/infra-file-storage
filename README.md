Grails plugin: infra-file-storage
====================

Stores files on Amazon S3 when deployed, on localhost when testing/developing.

Motivation
---------------------

Commonly application uploads some files from users. It may be user photos, price lists, videos, whatever.
During development phase, it's better to simply store files in a local directory. But on production it's possibly needed to
store files in the cloud, on CDN, on another server, etc. If you don't need it now, you probably will in the future.

This plugin provides you with a consistent API (using DI) to work with uploaded files in Grails. By default, it stores files
locally on development, and on Amazon S3 (with CloudFront support) when deployed.

You can store files, retrieve accessible file urls, update files with a single `fileStorageService` bean.

Also, due to the common task of working with several
files linked to a single object (e.g. domain), plugin provides `FileHolder` interface and shortcut methods.

Usage
---------------------

```groovy
// inject a bean
def fileStorageService

def storeSomething() {
    // any file is identified with:
    // - path (it may be domain-object-generated, like "post_files/${post.id}/")
    // - [optional] file name (for concrete file)
    // - [optional] bucket name (Amazon S3 bucket or similar config option for storage implementation)
    File smth = new File()
    fileStorageService.store(smth, "path/where/to/store", "filename_or_null", "bucketOrWorkspaceName_or_null")

    String fullHttpUrl = fileStorageService.getUrl("path/where/to/store", "filename_or_null", "bucketOrWorkspaceName_or_null")

    fileStorageService.delete("path/where/to/store", "filename_or_null", "bucketOrWorkspaceName_or_null")
}

// or you may implement FileHolder interface to ease working with files
class Post implements FileHolder {
    // Store file names some way, e.g. in Mongo embedded list, or serialized, or ghm
    List<String> fileNames = []

    // Each post has its own path
    String getFilesPath() {"post_files/${id}"}

    // Posts files are stored in default bucket
    String getFilesBucket() {null}
}

def storePostFile(File f, Post p) {
    fileStorageService.store(f, p, null)
    p.fileNames[] = f.name

    fileStorageService.getUrl(p, f.name)
    fileStorageService.delete(p)
}
```

Installation
---------------------

Clone plugin sources (you may fork it at first, and that'll be great) and set sources directory in `BuildConfig
.groovy`:

    grails.plugin.location.'infra-file-storage' = "infra-file-storage"

You will need valid S3 credentials to deploy the plugin.

And notice: you shouldn't call `grails install-plugin` or add direct dependency to `BuildConfig.groovy`!

Otherwise, you may download packaged version of the plugin and call

`grails install-plugin infra-file-storage.0.1.zip`

Configuration
---------------------

At first, after installation, you should assign your Config.groovy:

``` groovy
    mirari {
        infra {
            file {
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
```

Then just use `fileStorageService` or `fileStorage` Spring bean to store your files.

If you want to change environment-specified behaviour of file storage, override `fileStorage` Spring bean in `resources.groovy`:


```groovy
    fileStorage(FileStorageHolder) {
        storage = ref(Environment.isWarDeployed() ? "s3FileStorage" : "localFileStorage")
    }
```