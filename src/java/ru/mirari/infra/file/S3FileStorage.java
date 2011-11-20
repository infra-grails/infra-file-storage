package ru.mirari.infra.file;

import groovy.util.ConfigObject;
import org.apache.log4j.Logger;
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
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

  private final String urlRootSuffix = ".s3.amazonaws.com/";

  @Autowired
  S3FileStorage(GrailsApplication grailsApplication) throws S3ServiceException {
    ConfigObject config = (ConfigObject)grailsApplication.getConfig().get("mirari");
    config = (ConfigObject)config.get("infra");
    config = (ConfigObject)config.get("file");
    Map s3Conf = ((ConfigObject)config.get("s3")).flatten();

    s3Service = new RestS3Service(
        new AWSCredentials(
            s3Conf.get("accessKey").toString(),
            s3Conf.get("secretKey").toString()
        )
    );
    defaultBucket = s3Conf.get("defaultBucket").toString();

    String urlRootBuilder = s3Conf.get("urlRoot").toString();

    if (urlRootBuilder == null || urlRootBuilder.isEmpty()) urlRootBuilder = defaultBucket.concat(urlRootSuffix);
    if (!urlRootBuilder.endsWith("/")) urlRootBuilder = urlRootBuilder.concat("/");
    urlRoot = urlRootBuilder;
  }

  public void store(final File file, String path, String filename, String bucket) throws IOException, NoSuchAlgorithmException, ServiceException {
    org.jets3t.service.model.S3Object o = new S3Object(file);
    o.setKey(buildObjectKey(path, filename == null || filename.isEmpty() ? file.getName() : filename));
    // TODO: file might have temporary address and be private
    o.setAcl(org.jets3t.service.acl.AccessControlList.REST_CANNED_PUBLIC_READ);
    s3Service.putObjectMaybeAsMultipart(getBucket(bucket), o, 5242880);
    log.info("Saved ${o.key} to s3 storage");
  }


  public void delete(String path, String filename, String bucket) throws ServiceException {
    s3Service.deleteObject(getBucket(bucket), buildObjectKey(path, filename));
  }


  public String getUrl(String path, String filename, String bucket) {
    if (bucket == null || bucket.isEmpty() || bucket.equals(defaultBucket)) {
      return urlRoot.concat(buildObjectKey(path, filename));
    } else {
      return bucket.concat(urlRootSuffix).concat(buildObjectKey(path, filename));
    }
  }

  private String buildObjectKey(String relativePath, String filename) {
    if (!relativePath.endsWith("/")) {
      relativePath = relativePath.concat("/");
    }
    return relativePath.concat(filename);
  }

  private String getBucket(String bucket) {
    if (bucket == null || bucket.isEmpty()) return bucket;
    return defaultBucket;
  }
}
