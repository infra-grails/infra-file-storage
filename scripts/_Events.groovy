eventCreateWarStart = { warName, warFile ->
    if (grailsEnv == "production") {
        println "Removing infra-file-storage local directory"
        ant.delete(dir: "${stagingDir}/storage")
    }
}