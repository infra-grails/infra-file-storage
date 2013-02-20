package infra.file.storage.config

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.jets3t.service.CloudFrontService
import org.jets3t.service.impl.rest.httpclient.RestS3Service
import org.jets3t.service.security.AWSCredentials
import org.jets3t.service.security.ProviderCredentials

/**
 * @author alari
 * @since 2/18/13 4:11 PM
 */
class S3StorageConfig {
    final String defaultBucket;
    final String urlRoot;


    String urlRootSuffix = ".s3.amazonaws.com/";

    final Map<String, String> buckets;

    final private ProviderCredentials awsCredentials;

    boolean invalidateCloudFront = false

    S3StorageConfig(GrailsApplication grailsApplication) {
        ConfigObject config;
        Map s3Conf;
        try {
            config = (ConfigObject) grailsApplication.getConfig().get("plugin");
            config = (ConfigObject) config.get("infraFileStorage");
            s3Conf = ((ConfigObject) config.get("s3")).flatten();
        } catch (NullPointerException npe) {
            defaultBucket = null;
            urlRoot = null;
            buckets = null;
            return;
        }

        awsCredentials = new AWSCredentials(
                s3Conf.get("accessKey").toString(),
                s3Conf.get("secretKey").toString()
        );

        defaultBucket = s3Conf.get("defaultBucket").toString();

        String urlRootBuilder = s3Conf.get("urlRoot").toString();

        Map<String, String> _buckets = new HashMap<String, String>();

        if (((ConfigObject) config.get("s3")).get("buckets") instanceof ConfigObject) {
            Map bucketsConf = ((ConfigObject) ((ConfigObject) config.get("s3")).get("buckets")).flatten();
            for (Object k : bucketsConf.keySet()) {
                _buckets.put(k.toString(), bucketsConf.get(k).toString());
                if (!_buckets.get(k.toString()).endsWith("/")) {
                    _buckets.put(k.toString(), _buckets.get(k.toString()).concat("/"));
                }
            }
        }

        buckets = _buckets;

        if (urlRootBuilder == null || urlRootBuilder.isEmpty()) urlRootBuilder = defaultBucket.concat(urlRootSuffix);
        if (!urlRootBuilder.endsWith("/")) urlRootBuilder = urlRootBuilder.concat("/");
        urlRoot = urlRootBuilder;
    }

    public RestS3Service buildS3Service() {
        new RestS3Service(awsCredentials)
    }

    public CloudFrontService buildCloudFrontService() {
        new CloudFrontService(awsCredentials)
    }
}
