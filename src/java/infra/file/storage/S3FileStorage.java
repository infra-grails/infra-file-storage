package infra.file.storage;

import infra.file.storage.config.S3StorageConfig;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.NoSuchAlgorithmException;

/**
 * @author alari
 * @since 11/16/11 12:53 PM
 */
@Component
public class S3FileStorage extends FileStoragePrototype {
    final Logger log = Logger.getLogger(S3FileStorage.class);

    final RestS3Service s3Service;
    final CloudFrontService cloudFrontService;

    private S3StorageConfig config;

    @Autowired
    S3FileStorage(GrailsApplication grailsApplication) throws S3ServiceException, CloudFrontServiceException {
        config = new S3StorageConfig(grailsApplication);
        s3Service = config.buildS3Service();
        cloudFrontService = config.buildCloudFrontService();
    }

    @Override
    public String store(final MultipartFile file, String path, String filename, String bucket) throws Exception {
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

        return filename;
    }

    @Override
    public String store(final File file, String path, String filename, String bucket) throws IOException, NoSuchAlgorithmException, ServiceException {
        S3Object o = new S3Object(file);
        o.setKey(buildObjectKey(path, filename == null || filename.isEmpty() ? file.getName() : filename));

        o.setAcl(AccessControlList.REST_CANNED_PUBLIC_READ);

        bucket = getBucket(bucket);

        s3Service.putObjectMaybeAsMultipart(bucket, o, 5242880);
        log.info("Saved ${o.key} to s3 storage: ".concat(o.getKey()));

        // Invalidate objects
        invalidateCloudFront(o.getKey(), bucket);

        return filename;
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
        if (bucket == null || bucket.isEmpty() || bucket.equals(config.getDefaultBucket())) {
            return config.getUrlRoot().concat(buildObjectKey(path, filename));
        } else if (config.getBuckets().containsKey(bucket)) {
            return config.getBuckets().get(bucket).concat(buildObjectKey(path, filename));
        } else {
            return "http://".concat(bucket).concat(config.getUrlRootSuffix()).concat(buildObjectKey(path, filename));
        }
    }

    @Override
    public long getSize(String path, String filename, String bucket) throws Exception {
        return s3Service.getObjectDetails(getBucket(bucket), buildObjectKey(path, filename)).getContentLength();
    }

    @Override
    public File getFile(String path, String filename, String bucket) throws Exception {
        S3Object object = s3Service.getObject(getBucket(bucket), buildObjectKey(path, filename));
        File f = File.createTempFile(filename, "s3");

        ReadableByteChannel rbc = Channels.newChannel(object.getDataInputStream());
        FileOutputStream fos = new FileOutputStream(f);
        fos.getChannel().transferFrom(rbc, 0, 1 << 24);

        return f;
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
            log.error("Cannot invalidate object! ".concat(e.getErrorMessage()));
        }
    }

    private String buildObjectKey(String relativePath, String filename) {
        if (!relativePath.endsWith("/")) {
            relativePath = relativePath.concat("/");
        }
        return relativePath.concat(filename);
    }

    private String getBucket(String bucket) {
        return (bucket == null || bucket.isEmpty()) ? config.getDefaultBucket() : bucket;
    }
}
