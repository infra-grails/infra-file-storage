grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.project.repos.default = "internal-snapshot"

grails.project.dependency.distribution = {

    String serverRoot = "http://artifactory.dev/"

    remoteRepository(id: 'internal-snapshot', url: serverRoot + '/plugins-snapshot-local/') {
        authentication username: 'admin', password: 'password'
    }

    remoteRepository(id: 'internal-release', url: serverRoot + '/plugins-release-local/') {
        authentication username: 'admin', password: 'password'
    }
}

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
        excludes 'hibernate'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        grailsCentral()
        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        //mavenCentral()
        mavenLocal()
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
        mavenRepo "http://artifactory.dev/repo"
        grailsRepo "http://artifactory.dev/repo", "dev"
        mavenRepo "http://www.jets3t.org/maven2"
        mavenCentral()
    }
    dependencies {
        compile "net.java.dev.jets3t:jets3t:latest.release"

        test "org.spockframework:spock-grails-support:0.7-groovy-2.0"
    }

    plugins {
        build(":tomcat:$grailsVersion",
                ":release:latest.release") {
            export = false
        }

        //compile ":platform-core:latest.release"

        test(":spock:latest.release") {
            exclude "spock-grails-support"
        }
    }
}
