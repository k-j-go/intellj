package com.azunitech.search;

import com.azunitech.search.s3.ECSS3Client;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URISyntaxException;

@SpringBootTest
public class S3Test {
    @Autowired
    ECSS3Client ecss3Client;


    @Test
    public void contextLoads() throws URISyntaxException {
        ecss3Client.createBucket("test");
        ecss3Client.putObject("test", "test", "abcdefg");
        ecss3Client.listBucketObjects("test");
    }

}
