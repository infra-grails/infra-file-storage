package infra.file.storage

import grails.plugin.spock.IntegrationSpec
import org.springframework.core.io.ClassPathResource
import ru.mirari.infra.FileStorageService

class BasicFileStorageSpec extends IntegrationSpec {

    FileStorageService fileStorageService

    File file
    String path = "basic"
    String bucket = "tts"

    def setup() {
        file = new ClassPathResource("test.tmp", this.class).getFile();
    }

    def cleanup() {
        new File("web-app/f").deleteDir()
    }

    void "basic holder story"() {
        given:
        def holder = fileStorageService.getManager(path, bucket, false)
        String src = "web-app/f/${bucket}/${path}/test.tmp"

        expect:
        holder != null
        holder.bucket == bucket
        !new File(src).isFile()
        !holder.exists("test.tmp")

        when:
        holder.store(file)

        then:
        holder.fileNames == ["test.tmp"]
        holder.exists("test.tmp")
        holder.getSize("test.tmp") == file.length()
        holder.getUrl("test.tmp") == "/f/${bucket}/${path}/test.tmp"
        new File(src).isFile()
        new File(src).text == file.text

        when:
        holder.delete()

        then:
        !holder.fileNames
        !new File(src).isFile()
        !holder.exists("test.tmp")
    }

    void "wrapped with files domains story"() {
        given:
        def holder = fileStorageService.getManager(path, bucket, true)
        String src = "web-app/f/${bucket}/${path}/test.tmp"

        expect:
        holder != null
        holder instanceof DomainFilesManager
        holder.bucket == bucket
        holder.fileNames == []
        !new File(src).isFile()
        !holder.exists("test.tmp")

        when:
        holder.store(file)

        then:
        holder.fileNames == ["test.tmp"]
        fileStorageService.getManager(path, bucket, true).fileNames == ["test.tmp"]
        holder.exists("test.tmp")
        holder.getUrl("test.tmp") == "/f/${bucket}/${path}/test.tmp"
        new File(src).isFile()
        new File(src).text == file.text

        when:
        holder.delete()

        then:
        !holder.fileNames
        !fileStorageService.getManager(path, bucket, true).fileNames
        !new File(src).isFile()
        !holder.exists("test.tmp")
    }
}