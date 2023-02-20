package com.azunitech.pilot.clients.dynamodb;

import com.azunitech.pilot.clients.dynamodb.models.Customer;
import com.azunitech.pilot.clients.kinesis.ECSKinesisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.internal.waiters.ResponseOrException;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class ECSDynamoDBClient {
    private static final Logger logger = LoggerFactory.getLogger(ECSDynamoDBClient.class);

    private DynamoDbClient dynamoDbClient;

    private DynamoDbEnhancedClient enhancedClient;
    public ECSDynamoDBClient create(Region region, URI endPointUri) throws URISyntaxException {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
                "local",
                "local");
        dynamoDbClient = DynamoDbClient.builder()
                .region(region)
                .endpointOverride(endPointUri)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .httpClient(UrlConnectionHttpClient.builder().build())
                .build();

        enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();

        return this;
    }

    public String createTable(String tableName, String key) {
        DynamoDbWaiter dbWaiter = dynamoDbClient.waiter();
        CreateTableRequest request = CreateTableRequest.builder()
                .attributeDefinitions(AttributeDefinition.builder()
                        .attributeName(key)
                        .attributeType(ScalarAttributeType.S)
                        .build())
                .keySchema(KeySchemaElement.builder()
                        .attributeName(key)
                        .keyType(KeyType.HASH)
                        .build())
                .provisionedThroughput(ProvisionedThroughput.builder()
                        .readCapacityUnits(10L)
                        .writeCapacityUnits(10L)
                        .build())
                .tableName(tableName)
                .build();

        String newTable = "";
        try {
            CreateTableResponse response = dynamoDbClient.createTable(request);
            DescribeTableRequest tableRequest = DescribeTableRequest.builder()
                    .tableName(tableName)
                    .build();

            // Wait until the Amazon DynamoDB table is created.
            WaiterResponse<DescribeTableResponse> waiterResponse = dbWaiter.waitUntilTableExists(tableRequest);
            waiterResponse.matched().response().ifPresent(System.out::println);
            newTable = response.tableDescription().tableName();
            return newTable;

        } catch (DynamoDbException e) {
            logger.error(e.awsErrorDetails().errorMessage());
        }
        return "";
    }


    public String createTableComKey(String tableName) {
        CreateTableRequest request = CreateTableRequest.builder()
                .attributeDefinitions(AttributeDefinition.builder()
                                .attributeName("Language")
                                .attributeType(ScalarAttributeType.S)
                                .build(),
                        AttributeDefinition.builder()
                                .attributeName("Greeting")
                                .attributeType(ScalarAttributeType.S)
                                .build())
                .keySchema(KeySchemaElement.builder()
                                .attributeName("Language")
                                .keyType(KeyType.HASH)
                                .build(),
                        KeySchemaElement.builder()
                                .attributeName("Greeting")
                                .keyType(KeyType.RANGE)
                                .build())
                .provisionedThroughput(ProvisionedThroughput.builder()
                        .readCapacityUnits(10L)
                        .writeCapacityUnits(10L).build())
                .tableName(tableName)
                .build();

        String tableId = "";
        try {
            CreateTableResponse result = dynamoDbClient.createTable(request);
            tableId = result.tableDescription().tableId();
            return tableId;
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return "";
    }

    public void scanItems(String tableName) {

        try {
            ScanRequest scanRequest = ScanRequest.builder()
                    .tableName(tableName)
                    .build();

            ScanResponse response = dynamoDbClient.scan(scanRequest);
            for (Map<String, AttributeValue> item : response.items()) {
                Set<String> keys = item.keySet();
                for (String key : keys) {
                    System.out.println("The key name is " + key + "\n");
                    System.out.println("The value is " + item.get(key).s());
                }
            }

        } catch (DynamoDbException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void listAllTables(){

        boolean moreTables = true;
        String lastName = null;

        while(moreTables) {
            try {
                ListTablesResponse response = null;
                if (lastName == null) {
                    ListTablesRequest request = ListTablesRequest.builder().build();
                    response = dynamoDbClient.listTables(request);
                } else {
                    ListTablesRequest request = ListTablesRequest.builder()
                            .exclusiveStartTableName(lastName).build();
                    response = dynamoDbClient.listTables(request);
                }

                List<String> tableNames = response.tableNames();
                if (tableNames.size() > 0) {
                    for (String curName : tableNames) {
                        System.out.format("* %s\n", curName);
                    }
                } else {
                    System.out.println("No tables found!");
                    System.exit(0);
                }

                lastName = response.lastEvaluatedTableName();
                if (lastName == null) {
                    moreTables = false;
                }

            } catch (DynamoDbException e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
        System.out.println("\nDone!");
    }


    public void enhanced_CreateTable () {
        // Create a DynamoDbTable object
        DynamoDbTable<Customer> customerTable = enhancedClient.table("Customer", TableSchema.fromBean(Customer.class));
        // Create the table
        customerTable.createTable(builder -> builder
                .provisionedThroughput(b -> b
                        .readCapacityUnits(10L)
                        .writeCapacityUnits(10L)
                        .build())
        );

        logger.info("Waiting for table creation...");

        try (DynamoDbWaiter waiter = DynamoDbWaiter.create()) { // DynamoDbWaiter is Autocloseable
            ResponseOrException<DescribeTableResponse> response = waiter
                    .waitUntilTableExists(builder -> builder.tableName("Customer").build())
                    .matched();
            DescribeTableResponse tableDescription = response.response().orElseThrow(
                    () -> new RuntimeException("Customer table was not created."));
            // The actual error can be inspected in response.exception()
            logger.info(tableDescription.table().tableName() + " was created.");
        }
    }

    public void enhanced_putRecord() {
        try {
            DynamoDbTable<Customer> custTable = enhancedClient.table("Customer", TableSchema.fromBean(Customer.class));

            // Create an Instant value.
            LocalDate localDate = LocalDate.parse("2020-04-07");
            LocalDateTime localDateTime = localDate.atStartOfDay();
            Instant instant = localDateTime.toInstant(ZoneOffset.UTC);

            // Populate the Table.
            Customer custRecord = new Customer();
            custRecord.setCustName("Tom red");
            custRecord.setId("id101");
            custRecord.setEmail("tred@noserver.com");
            custRecord.setRegistrationDate(instant) ;

            // Put the customer data into an Amazon DynamoDB table.
            custTable.putItem(custRecord);

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("Customer data added to the table with id id101");
    }

    public static ECSDynamoDBClientBuilder builder(){
        return new ECSDynamoDBClientBuilder();
    }

    public static class ECSDynamoDBClientBuilder {

        private Region region;
        private URI endPointUri;

        public ECSDynamoDBClientBuilder setRegion(Region region) {
            this.region = region;
            return this;
        }

        public ECSDynamoDBClientBuilder setEndPoint(String endPoint) throws URISyntaxException {
            if (checkValidate.apply(endPoint)) {
                throw new URISyntaxException(endPoint, "invalid");
            }
            this.endPointUri = new URI(String.format("http://%s:4566", endPoint));
            return this;
        }

        public ECSDynamoDBClientBuilder setLocalStackEndPoint() throws URISyntaxException {
            String host = System.getenv("LOCALSTACK_HOSTNAME");
            return setEndPoint(host);
        }

        public ECSDynamoDBClient build() throws URISyntaxException {
            ECSDynamoDBClient client = new ECSDynamoDBClient();
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

        if (!HOST_REGEX_PATTERN.matcher(host).matches()) {
            return false;
        }

        if (!PORT_REGEX_PATTERN.matcher(port).matches()) {
            return false;
        }
        return true;
    };
}
