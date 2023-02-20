
package com.azunitech.pilot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * The module containing all dependencies required by the {@link App}.
 */
public class DependencyFactory {
    private static final Logger logger = LoggerFactory.getLogger(DependencyFactory.class);
    private DependencyFactory() {}

    /**
     * @return an instance of S3Client
     */
    public static S3Client s3Client() {
        String host = System.getenv("LOCALSTACK_HOSTNAME");
        String endpoint = String.format("http://%s:4566", host);
        logger.info("******************" + host);
        return S3Client.builder()
                       .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                       .region(Region.US_EAST_1)
                       .httpClientBuilder(UrlConnectionHttpClient.builder())
                       .build();
    }
}
