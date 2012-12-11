package ru.mirari.infra.file;

import groovy.util.ConfigObject;
import org.apache.log4j.Logger;
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.jets3t.service.CloudFrontService;
import org.jets3t.service.CloudFrontServiceException;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.ServiceException;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.model.cloudfront.Distribution;
import org.jets3t.service.model.cloudfront.Invalidation;
import org.jets3t.service.security.AWSCredentials;
import org.jets3t.service.security.ProviderCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author alari
 * @since 11/16/11 12:53 PM
 */
public class S3FileStorage extends FileStoragePrototype {
    private final String defaultBucket;
    private final String urlRoot;
    private final Logger log = Logger.getLogger(S3FileStorage.class);

    final RestS3Service s3Service;
    final CloudFrontService cloudFrontService;

    private final String urlRootSuffix = ".s3.amazonaws.com/";

    private final Map<String, String> buckets;

    @Autowired
    S3FileStorage(GrailsApplication grailsApplication) throws S3ServiceException, CloudFrontServiceException {
        ConfigObject config;
        Map s3Conf;
        try {
            config = (ConfigObject) grailsApplication.getConfig().get("mirari");
            config = (ConfigObject) config.get("infra");
            config = (ConfigObject) config.get("file");
            s3Conf = ((ConfigObject) config.get("s3")).flatten();
        } catch (NullPointerException npe) {
            defaultBucket = null;
            urlRoot = null;
            s3Service = null;
            cloudFrontService = null;
            buckets = null;
            return;
        }

        ProviderCredentials awsCredentials = new AWSCredentials(
                s3Conf.get("accessKey").toString(),
                s3Conf.get("secretKey").toString()
        );

        s3Service = new RestS3Service(awsCredentials);
        cloudFrontService = new CloudFrontService(awsCredentials);

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

    @Override
    public void store(final MultipartFile file, String path, String filename, String bucket) throws Exception {
        if (filename == null || filename.isEmpty()) filename = file.getOriginalFilename();

        S3Object o = new S3Object();
        o.setKey(buildObjectKey(path, filename));
        o.setAcl(AccessControlList.REST_CANNED_PUBLIC_READ);

        // Pushing as stream
        o.setDataInputStream(file.getInputStream());
        o.setContentLength(file.getSize());
        o.setContentType(file.getContentType());

        bucket = getBucket(bucket);

        s3Service.putObjectMaybeAsMultipart(bucket, o, 5242880);
        log.info("Saved to s3 storage as stream: ".concat(o.getKey()));

        invalidateCloudFront(o.getKey(), bucket);
    }

    @Override
    public void store(final File file, String path, String filename, String bucket) throws IOException, NoSuchAlgorithmException, ServiceException {
        S3Object o = new S3Object(file);
        o.setKey(buildObjectKey(path, filename == null || filename.isEmpty() ? file.getName() : filename));

        o.setAcl(AccessControlList.REST_CANNED_PUBLIC_READ);

        bucket = getBucket(bucket);

        s3Service.putObjectMaybeAsMultipart(bucket, o, 5242880);
        log.info("Saved ${o.key} to s3 storage: ".concat(o.getKey()));

        // Invalidate objects
        invalidateCloudFront(o.getKey(), bucket);
    }


    @Override
    public void delete(String path, String filename, String bucket) throws ServiceException {
        s3Service.deleteObject(getBucket(bucket), buildObjectKey(path, filename));
    }

    @Override
    public boolean exists(String path, String filename, String bucket) throws Exception {
        return s3Service.isObjectInBucket(getBucket(bucket), buildObjectKey(path, filename));
    }

    @Override
    public String getUrl(String path, String filename, String bucket) {
        if (bucket == null || bucket.isEmpty() || bucket.equals(defaultBucket)) {
            return urlRoot.concat(buildObjectKey(path, filename));
        } else if (buckets.containsKey(bucket)) {
            return buckets.get(bucket).concat(buildObjectKey(path, filename));
        } else {
            return "http://".concat(bucket).concat(urlRootSuffix).concat(buildObjectKey(path, filename));
        }
    }

    private void invalidateCloudFront(final String objectKey, final String bucket) {
        try {
            String[] objectKeys = new String[]{objectKey};
            Distribution[] bucketDistributions = cloudFrontService.listDistributions(bucket);
            for (Distribution bucketDistribution : bucketDistributions) {
                Invalidation invalidation = cloudFrontService.invalidateObjects(
                        bucketDistribution.getId(),
                        objectKeys,
                        "" + System.currentTimeMillis() // Caller reference - a unique string value
                );
                log.info(invalidation);
            }
        } catch (CloudFrontServiceException e) {
            log.error("Cannot invalidate object!", e);
        }
    }

    private String buildObjectKey(String relativePath, String filename) {
        if (!relativePath.endsWith("/")) {
            relativePath = relativePath.concat("/");
        }
        return relativePath.concat(filename);
    }

    private String getBucket(String bucket) {
        return (bucket == null || bucket.isEmpty()) ? defaultBucket : bucket;
    }
}
