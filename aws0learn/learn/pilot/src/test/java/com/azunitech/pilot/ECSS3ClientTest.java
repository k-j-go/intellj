package com.azunitech.pilot;

import com.azunitech.pilot.clients.s3.ECSS3Client;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.regions.Region;

import java.net.URISyntaxException;

public class ECSS3ClientTest {
    @Test
    public void generalTest() throws URISyntaxException {
        ECSS3Client ecsClient = ECSS3Client.builder()
                .region(Region.US_EAST_1)
                .host("127.0.0.1")
                .build();

        ecsClient.createBucket("test");
        ecsClient.listBucketObjects("test");

    }


}
