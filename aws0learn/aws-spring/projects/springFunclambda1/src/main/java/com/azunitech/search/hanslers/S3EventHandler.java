package com.azunitech.search.hanslers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.azunitech.search.hanslers.clients.s3.ECSS3Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;

import java.net.URISyntaxException;
import java.util.Optional;

public class S3EventHandler implements RequestHandler<S3Event, String> {
    static final Logger log = LoggerFactory.getLogger(S3EventHandler.class);

    @Override
    public String handleRequest(S3Event s3Event, Context context) {
        log.info("Lambda function is invoked: Processing the uploads........." + s3Event.toJson());
        String BucketName = s3Event.getRecords()
                .get(0)
                .getS3()
                .getBucket()
                .getName();
        String FileName = s3Event.getRecords()
                .get(0)
                .getS3()
                .getObject()
                .getKey();
        log.info("File - " + FileName + " uploaded into " +
                BucketName + " bucket at " + s3Event.getRecords()
                .get(0)
                .getEventTime());
        try {
            ECSS3Client ecs = ECSS3Client.builder()
                    .region(Region.US_EAST_1)
                    .localStackEndPoint()
                    .build();
            Optional<byte[]> bytes = ecs.getObjectBytes(BucketName, FileName);
            bytes.ifPresent( x -> {
                //log.info("File Contents : " + StreamUtils.copyToString(x, StandardCharsets.UTF_8));
                log.info("File Contents : " + new String(x));
            });
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
}