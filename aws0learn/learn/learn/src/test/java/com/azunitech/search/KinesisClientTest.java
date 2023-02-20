package com.azunitech.search;

import com.azunitech.search.kinesis.ECSAsyncKinesisClient;
import com.azunitech.search.kinesis.ECSKinesisClient;
import com.azunitech.search.kinesis.models.Address;
import com.github.javafaker.Faker;
import com.github.javafaker.service.FakeValuesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class KinesisClientTest {
    @Autowired
    ECSKinesisClient ecsKinesisClient;

    @Autowired
    FakeValuesService fakeValuesService;

    @Autowired
    ECSAsyncKinesisClient ecsAsyncKinesisClient;
    @Autowired
    Faker faker;

    @Test
    public void generalTest() {
        com.github.javafaker.Address address = faker.address();
        //ecsKinesisClient.createStream("test");
        ecsKinesisClient.putAddressRecords("test", Address.builder()
                .address(address.streetAddress())
                .state(address.state())
                .zip(address.zipCode())
                .city(address.city())
                .state(address.state())
                .build());
        ecsKinesisClient.getAddress("test");
        //ecsAsyncKinesisClient.create("arn:aws:kinesis:us-east-1:000000000000:stream/test", "con3");
    }
}
