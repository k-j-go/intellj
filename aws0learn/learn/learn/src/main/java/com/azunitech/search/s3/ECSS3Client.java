package com.azunitech.search.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ECSS3Client {

    @Value("${localstack.region:us-east-1}")
    private String region;

    @Value("${localstack.endpoint: http://127.0.0.1:4566}")
    private String endpoint;


    S3Client s3Client;

    public ECSS3Client() {
    }

    @PostConstruct
    public void create() throws URISyntaxException {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
                "local",
                "local");
        Region region = Region.US_EAST_1;
        s3Client = S3Client.builder()
                .region(region)
                .endpointOverride(new URI(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }

    public void createBucket(String bucketName) {
        CreateBucketResponse res = s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName)
                .build());
    }

    public void putObject(String bucketName, String objectKey, String msg) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("x-amz-meta-myVal", "test");
        PutObjectRequest putOb = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .metadata(metadata)
                .build();

        byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
        PutObjectResponse response = s3Client.putObject(putOb, RequestBody.fromBytes(bytes));
    }

    public  void listBucketObjects(String bucketName ) {

        try {
            ListObjectsRequest listObjects = ListObjectsRequest
                    .builder()
                    .bucket(bucketName)
                    .build();

            ListObjectsResponse res = s3Client.listObjects(listObjects);
            List<S3Object> objects = res.contents();
            for (S3Object myValue : objects) {
                System.out.print("\n The name of the key is " + myValue.key());
                System.out.print("\n The object is " + calKb(myValue.size()) + " KBs");
                System.out.print("\n The owner is " + myValue.owner());
            }

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    private static long calKb(Long val) {
        return val/1024;
    }
}
