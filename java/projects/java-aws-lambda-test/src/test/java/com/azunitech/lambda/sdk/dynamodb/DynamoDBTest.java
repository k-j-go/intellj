package com.azunitech.lambda.sdk.dynamodb;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class DynamoDBTest {
    private String table_name = "testtable";
    DynamoDbClient ddbClient;
    AmazonDynamoDB client;
    @BeforeEach
    public void setUp() throws URISyntaxException {
        Region region = Region.US_EAST_1;
        AwsClientBuilder.EndpointConfiguration ep = new AwsClientBuilder.EndpointConfiguration(
                "http://127.0.0.1:4566", Regions.US_WEST_2.toString());

        ddbClient = DynamoDbClient.builder()
                .region(region)
                .endpointOverride(new URI("http://127.0.0.1:4566"))
                .build();

        client = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(
                new AwsClientBuilder.EndpointConfiguration("http://localhost:4566", "us-west-2"))
                .build();
    }

    @Test
    public void createTableTest() {
        CreateTableRequest request = new CreateTableRequest()
                .withAttributeDefinitions(new AttributeDefinition(
                        "Name", ScalarAttributeType.S))
                .withKeySchema(new KeySchemaElement("Name", KeyType.HASH))
                .withProvisionedThroughput(new ProvisionedThroughput(
                        new Long(10), new Long(10)))
                .withTableName(table_name);

        try {
            CreateTableResult result = client.createTable(request);
            System.out.println(result.getTableDescription().getTableName());
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        System.out.println("Done!");
    }

    @Test
    public void putItemTest(){
        ArrayList<String[]> extra_fields = new ArrayList<String[]>();


    }
}
