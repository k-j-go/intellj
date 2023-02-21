package com.azunitech.search.ses;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.ses.SesClient;

import java.net.URI;
import java.net.URISyntaxException;

public class ECSSesClient {
    private SesClient sesClient;

    public void create(Region region, String endPointUrl) throws URISyntaxException {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
                "local",
                "local");
        sesClient = SesClient.builder()
                .region(region)
                .endpointOverride(new URI(endPointUrl))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .httpClient(UrlConnectionHttpClient.builder()
                        .build())
                .build();
    }

    public static ECSSesClientBuilder builder() {
        return new ECSSesClientBuilder();
    }

    public static class ECSSesClientBuilder {

    }
}
