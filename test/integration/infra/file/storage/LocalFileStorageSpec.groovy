package infra.file.storage

import grails.plugin.spock.IntegrationSpec
import org.springframework.core.io.ClassPathResource
import ru.mirari.infra.FileStorageService
import spock.lang.Stepwise

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@Stepwise
class LocalFileStorageSpec extends IntegrationSpec {

    FileStorageService fileStorageService

    File file

    def setup() {
        file = new ClassPathResource("test.tmp", this.class).getFile();
    }

    def cleanup() {
        //new File("web-app/f").deleteDir()
    }

    @FilesHolder(
    path = {
        assert id
        "test/" + id
    },
    allowedExtensions = ["tmp"]
    )
    private static class Holder {
        String id
        List<String> fileNames
    }

    void "test holder is created"() {
        given:
        Holder holder = new Holder()

        expect:
        holder != null
    }

    void "test file is stored and deleted"() {
        given:
        Holder holder = new Holder(id: 0)
        String src = "web-app/f/storage/test/0/test.tmp"

        expect:
        fileStorageService != null
        !new File(src).isFile()
        holder != null
        !fileStorageService.exists(holder, "test.tmp")

        when:
        fileStorageService.store(holder, file)

        then:
        holder.fileNames == ["test.tmp"]
        new File(src).isFile()
        new File(src).text == file.text
        fileStorageService.exists(holder, "test.tmp")
        fileStorageService.getUrl(holder) == "/f/storage/test/0/test.tmp"
        fileStorageService.getUrl(holder, "test.tmp") == "/f/storage/test/0/test.tmp"

        when:
        fileStorageService.delete(holder)

        then:
        !holder.fileNames
        !new File(src).isFile()
        !fileStorageService.exists(holder, "test.tmp")
    }
}
