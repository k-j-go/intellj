package com.azunitech.pilot;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.azunitech.pilot.clients.kinesis.ECSKinesisClient;
import com.azunitech.pilot.clients.kinesis.models.Address;
import com.azunitech.pilot.clients.s3.ECSS3Client;
import com.azunitech.pilot.clients.sns.ECSSnsClient;
import com.azunitech.pilot.clients.sqs.ECSSqsClient;
import com.github.javafaker.Faker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.net.URISyntaxException;

public class S3EventHandler implements RequestHandler<S3Event, Integer> {
    private static final Logger logger = LoggerFactory.getLogger(S3EventHandler.class);
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public Integer handleRequest(S3Event s3Event, Context context) {
        String bucket = s3Event.getRecords().get(0).getS3().getBucket().getName();
        String key = s3Event.getRecords().get(0).getS3().getObject().getKey();

        GetObjectRequest get = GetObjectRequest.builder().bucket(bucket)
                .key(key)
                .build();

        try {
            ECSS3Client ecss3Client = ECSS3Client.builder().setRegion(Region.US_EAST_1).setLocalStackEndPoint().build();
            ecss3Client.listBucketObjects("mybucket");
            ecss3Client.getObjectBytes(bucket, key).ifPresent( x -> {
                logger.info(new String(x));
            });
            ecss3Client.createBucket("target");
            ecss3Client.copyBucketObject(bucket, key, "target");
        } catch (URISyntaxException e) {
            logger.error(e.getMessage());
        }

        try {
            ECSKinesisClient ecsKinesisClient = ECSKinesisClient.builder().setRegion(Region.US_EAST_1).setLocalStackEndPoint().build();
            ecsKinesisClient.createStream("mytopic");
            Faker faker = new Faker();
            com.github.javafaker.Address address = faker.address();
            ecsKinesisClient.putAddressRecords("mytopic", Address.builder()
                    .address(address.streetAddress())
                    .state(address.state())
                    .zip(address.zipCode())
                    .city(address.city())
                    .state(address.state())
                    .build());

        } catch (URISyntaxException e) {
            logger.error(e.getMessage());
        }


        try {
            ECSSnsClient ecsSnsClient = ECSSnsClient.builder()
                    .setRegion(Region.US_EAST_1)
                    .setLocalStackEndPoint()
                    .build();
            if (!ecsSnsClient.checkSNSTopicExists("mytopic")) {
                String arn = ecsSnsClient.createTopic("mytopic");
                ecsSnsClient.listSNSTopics();
                ecsSnsClient.pubTopic("mytopic", arn);
            }
        } catch (URISyntaxException e) {
            logger.error(e.getMessage());
        }

        try {
            ECSSqsClient sqsClient = ECSSqsClient.builder()
                    .setRegion(Region.US_EAST_1)
                    .setLocalStackEndPoint()
                    .build();
            String url = sqsClient.createQueue("mysqs");
            logger.info("mysqs url {}", url);
            sqsClient.sendBatchMessages(url);
        } catch (URISyntaxException e) {
            logger.error(e.getMessage());
        }

        return context.getRemainingTimeInMillis();
    }
}