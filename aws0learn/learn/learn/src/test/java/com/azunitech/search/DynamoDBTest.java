package com.azunitech.search;

import com.azunitech.search.dynamodb.ECSDynamoDBClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URISyntaxException;

@SpringBootTest
public class DynamoDBTest {
    @Autowired
    ECSDynamoDBClient ecsDynamoDBClient;

    @Test
    public void generalTest() throws URISyntaxException {
        ecsDynamoDBClient.process();
        //ecsDynamoDBClient.createTable("test", "id");
        //ecsDynamoDBClient.createTableComKey("language");
        ecsDynamoDBClient.scanItems("language");
        ecsDynamoDBClient.listAllTables();
    }
}
