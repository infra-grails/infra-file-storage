package infra.file.storage.config

import org.codehaus.groovy.grails.commons.GrailsApplication

/**
 * @author alari
 * @since 2/18/13 4:11 PM
 */
class LocalStorageConfig {
    String urlRoot
    String localRoot = "./web-app/f/"
    String defaultBucket = "storage"

    LocalStorageConfig(GrailsApplication grailsApplication) {
        Map localConf;
        ConfigObject config;

        localConf = grailsApplication.config?.plugin?.infraFileStorage?.local?.flatten()
        if (!localConf) {
            urlRoot = grailsApplication.config.grails.serverURL.toString().concat("/f/")
            if (urlRoot.equals("[:]/f/")) urlRoot = "/f/";
            return;
        }

        if (localConf.containsKey("localRoot")) localRoot = localConf.localRoot.toString().isEmpty() ? localRoot : localConf.localRoot.toString();
        if (localConf.containsKey("defaultBucket")) defaultBucket = localConf.defaultBucket.toString();

        if (localConf.containsKey("urlRoot")) urlRoot = localConf.urlRoot.toString();

        if (urlRoot == null || urlRoot.isEmpty()) {
            urlRoot = grailsApplication.config.grails.serverURL?.toString();
            if (urlRoot.equals("[:]")) urlRoot = "/";
            if (!urlRoot.endsWith("/")) urlRoot = urlRoot.concat("/");
            urlRoot = urlRoot.concat("f/");
        }
        if (!urlRoot.endsWith("/")) {
            urlRoot = urlRoot.concat("/");
        }
    }
}
