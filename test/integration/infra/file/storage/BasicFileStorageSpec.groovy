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
        def holder = fileStorageService.getHolder(path, bucket)
        String src = "web-app/f/${bucket}/${path}/test.tmp"

        expect:
        holder != null
        holder.bucket == bucket
        new File(src).isFile() == false
        holder.exists("test.tmp") == false

        when:
        holder.store(file)

        then:
        holder.fileNames == ["test.tmp"]
        holder.exists("test.tmp") == true
        holder.getUrl("test.tmp") == "/f/${bucket}/${path}/test.tmp"
        new File(src).isFile() == true
        new File(src).text == file.text

        when:
        holder.delete()

        then:
        !holder.fileNames
        new File(src).isFile() == false
        !holder.exists("test.tmp")
    }
}