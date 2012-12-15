Grails plugin: infra-file-storage
====================

Stores files on Amazon S3 (or another storage) when deployed, on localhost when testing/developing.

Plugin is devoted to become a platform solution to store files in Grails.

Usage
---------------------

Take a look on [Spock Integration tests](https://github.com/alari/infra-file-storage/tree/master/test/integration/infra/file/storage)

There are two ways to use plugin:

- Use basic files holder, accessible with `fileStorageService.getHolder(path, bucket=null)`

Example: get and save something

```groovy
    @Autowired
    def fileStorageService
     // ...
    def holder = fileStorageService.getHolder("test")
    holder.store((File)file)
    assert holder.exists(file.name)
    return holder.getUrl(file.name)
```

- Implement your own files mapping logic with `@FilesHolder` annotation ([src/groovy/ru/mirari/infra/file/FilesHolder.groovy])

Example: files holder domain class

```groovy
@FilesHolder(path={id})
DomainHolder {
    static hasMany = [fileNames: String]
}
```

Configuration
---------------------

At first, you should assign your Config.groovy:

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

Then just use `fileStorageService` Spring bean to manage your files.

If you want to change environment-specified behaviour of file storage, override `fileStorage` Spring bean in `resources.groovy`:


```groovy
    fileStorage(FileStorageHolder) {
        storage = ref(Environment.isWarDeployed() ? "s3FileStorage" : "localFileStorage")
    }
```

TODOs
-----------------------

- Create basic taglibs to retrieve files
- Substitute particular storage strategies (s3, rackspace, ...) into separate plugins to clean build dependencies

Companion plugins
-----------------------

[Images Storage & Resizing](https://github.com/alari/infra-images)
