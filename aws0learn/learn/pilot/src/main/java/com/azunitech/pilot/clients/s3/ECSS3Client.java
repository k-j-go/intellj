package com.azunitech.pilot.clients.s3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

public class ECSS3Client {
    private static final Logger logger = LoggerFactory.getLogger(ECSS3Client.class);

    S3Client s3Client;

    public ECSS3Client create(Region region, URI endPointUri) {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
                "local",
                "local");
        s3Client = S3Client.builder()
                .region(region)
                .endpointOverride(endPointUri)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .httpClient(UrlConnectionHttpClient.builder()
                        .build())
                .build();
        return this;
    }

    public void createBucket(String bucketName) {
        CreateBucketResponse res = s3Client.createBucket(CreateBucketRequest.builder()
                .bucket(bucketName)
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

    public String copyBucketObject(String fromBucket, String objectKey, String toBucket) {

        CopyObjectRequest copyReq = CopyObjectRequest.builder()
                .sourceBucket(fromBucket)
                .sourceKey(objectKey)
                .destinationBucket(toBucket)
                .destinationKey(objectKey)
                .build();

        try {
            CopyObjectResponse copyRes = s3Client.copyObject(copyReq);
            return copyRes.copyObjectResult()
                    .toString();

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails()
                    .errorMessage());
            System.exit(1);
        }
        return "";
    }

    public Optional<byte[]> getObjectBytes(String bucketName, String keyName) {
        try {
            GetObjectRequest objectRequest = GetObjectRequest
                    .builder()
                    .key(keyName)
                    .bucket(bucketName)
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(objectRequest);
            return Optional.of(objectBytes.asByteArray());

        } catch (S3Exception e) {
            logger.error(e.awsErrorDetails()
                    .errorMessage());
            return Optional.empty();
        }
    }

    public void listBucketObjects(String bucketName) {

        try {
            ListObjectsRequest listObjects = ListObjectsRequest
                    .builder()
                    .bucket(bucketName)
                    .build();

            ListObjectsResponse res = s3Client.listObjects(listObjects);
            List<S3Object> objects = res.contents();
            for (S3Object myValue : objects) {
                logger.info("last modified time {}", instantToString.apply(myValue.lastModified()));
                logger.info("bucket key {}", myValue.key());
                logger.info("bucket value size {} Kbs", calKb(myValue.size()));
                logger.info("Owner {}", myValue.owner());
            }

        } catch (S3Exception e) {
            logger.error(e.awsErrorDetails()
                    .errorMessage());
        }
    }

    private long calKb(Long val) {
        return val / 1024;
    }

    public static ECSS3ClientBuilder builder() {
        return new ECSS3ClientBuilder();
    }

    public static class ECSS3ClientBuilder {
        private Region region;
        private URI endPointUri;

        public ECSS3ClientBuilder setRegion(Region region) {
            this.region = region;
            return this;
        }

        public ECSS3ClientBuilder setEndPoint(String endPoint) throws URISyntaxException {
            if (checkValidate.apply(endPoint)) {
                throw new URISyntaxException(endPoint, "invalid");
            }
            this.endPointUri = new URI(String.format("http://%s:4566", endPoint));
            return this;
        }

        public ECSS3ClientBuilder setLocalStackEndPoint() throws URISyntaxException {
            String host = System.getenv("LOCALSTACK_HOSTNAME");
            return setEndPoint(host);
        }

        public ECSS3Client build() {
            ECSS3Client client = new ECSS3Client();
            return client.create(this.region, this.endPointUri);
        }
    }

    static java.util.function.Function<String, Boolean> checkValidate = x -> {
        final String HOST_PATTERN =
                "^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9-]*[a-zA-Z0-9])\\.)*" +
                        "([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9-]*[A-Za-z0-9])$";

        final String PORT_PATTERN =
                "^\\d{1,5}$";

        final Pattern HOST_REGEX_PATTERN = Pattern.compile(HOST_PATTERN);
        final Pattern PORT_REGEX_PATTERN = Pattern.compile(PORT_PATTERN);

        String[] parts = x.split(":");
        if (parts.length != 2) {
            return false;
        }

        String host = parts[0];
        String port = parts[1];

        if (!HOST_REGEX_PATTERN.matcher(host)
                .matches()) {
            return false;
        }

        if (!PORT_REGEX_PATTERN.matcher(port)
                .matches()) {
            return false;
        }
        return true;
    };

    Function<Instant, String> instantToString = instant -> {
        Date date = Date.from(instant);
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss,SSS");
        return formatter.format(date);
    };
}
