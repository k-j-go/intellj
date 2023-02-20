package com.azunitech.pilot.clients.kinesis;

import com.azunitech.pilot.clients.kinesis.models.Address;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ECSKinesisClient {

    KinesisClient kinesisClient;

    public ECSKinesisClient create(Region region, URI endpoint) throws URISyntaxException {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create("local",
                "local");
        kinesisClient = KinesisClient.builder()
                .region(region)
                .endpointOverride(endpoint)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .httpClient(UrlConnectionHttpClient.builder()
                        .build())
                .build();
        return this;
    }

    public void putAddressRecords(String streamName, Address address) {
        byte[] bytes = address.toJsonAsBytes();

        PutRecordRequest request = PutRecordRequest.builder()
                .partitionKey(address.getAddress() + "-" + address.getCity() + "-" + address.getState()) // We use the ticker symbol as the partition key, explained in the Supplemental Information section below.
                .streamName(streamName)
                .data(SdkBytes.fromByteArray(bytes))
                .build();

        try {
            kinesisClient.putRecord(request);
        } catch (KinesisException e) {
            System.err.println(e.getMessage());
        }
    }

    private void validateStream(String streamName) {
        try {
            DescribeStreamRequest describeStreamRequest = DescribeStreamRequest.builder()
                    .streamName(streamName)
                    .build();

            DescribeStreamResponse describeStreamResponse = kinesisClient.describeStream(describeStreamRequest);

            if (!describeStreamResponse.streamDescription()
                    .streamStatus()
                    .toString()
                    .equals("ACTIVE")) {
                System.err.println("Stream " + streamName + " is not active. Please wait a few moments and try again.");
                System.exit(1);
            }

        } catch (KinesisException e) {
            System.err.println("Error found while describing the stream " + streamName);
            System.err.println(e);
            System.exit(1);
        }
    }

    public void getAddress(String streamName) {

        String shardIterator;
        String lastShardId = null;

        // Retrieve the Shards from a Stream
        DescribeStreamRequest describeStreamRequest = DescribeStreamRequest.builder()
                .streamName(streamName)
                .build();

        List<Shard> shards = new ArrayList<>();
        DescribeStreamResponse streamRes;
        do {
            streamRes = kinesisClient.describeStream(describeStreamRequest);
            shards.addAll(streamRes.streamDescription()
                    .shards());

            if (shards.size() > 0) {
                lastShardId = shards.get(shards.size() - 1)
                        .shardId();
            }
        } while (streamRes.streamDescription()
                .hasMoreShards());

        GetShardIteratorRequest itReq = GetShardIteratorRequest.builder()
                .streamName(streamName)
                .shardIteratorType("TRIM_HORIZON")
                .shardId(lastShardId)
                .build();

        GetShardIteratorResponse shardIteratorResult = kinesisClient.getShardIterator(itReq);
        shardIterator = shardIteratorResult.shardIterator();

        // Continuously read data records from shard.
        List<Record> records;

        // Create new GetRecordsRequest with existing shardIterator.
        // Set maximum records to return to 1000.
        GetRecordsRequest recordsRequest = GetRecordsRequest.builder()
                .shardIterator(shardIterator)
                .limit(1000)
                .build();

        GetRecordsResponse result = kinesisClient.getRecords(recordsRequest);

        // Put result into record list. Result may be empty.
        records = result.records();

        // Print records
        for (Record record : records) {
            SdkBytes byteBuffer = record.data();
            System.out.printf("Seq No: %s - %s%n",
                    record.sequenceNumber(),
                    new String(byteBuffer.asByteArray()));
        }
    }

    public void addShards(String name, int goalShards) {
        try {
            UpdateShardCountRequest request = UpdateShardCountRequest.builder()
                    .scalingType("UNIFORM_SCALING")
                    .streamName(name)
                    .targetShardCount(goalShards)
                    .build();

            UpdateShardCountResponse response = kinesisClient.updateShardCount(request);
            System.out.println(response.streamName() + " has updated shard count to " + response.currentShardCount());

        } catch (KinesisException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public void createStream(String streamName) {

        try {
            CreateStreamRequest streamReq = CreateStreamRequest.builder()
                    .streamName(streamName)
                    .shardCount(1)
                    .build();

            kinesisClient.createStream(streamReq);

        } catch (KinesisException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public String regConsumer(String streamARN, String consumerName) {

        try {
            RegisterStreamConsumerRequest regCon = RegisterStreamConsumerRequest.builder()
                    .consumerName(consumerName)
                    .streamARN(streamARN)
                    .build();

            RegisterStreamConsumerResponse resp = kinesisClient.registerStreamConsumer(regCon);
            return resp.consumer()
                    .consumerARN();

        } catch (KinesisException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return "";
    }

    public static ECSKinesisClientBuilder builder() {
        return new ECSKinesisClientBuilder();
    }

    public static class ECSKinesisClientBuilder {

        private Region region;
        private URI endPointUri;

        public ECSKinesisClientBuilder region(Region region) {
            this.region = region;
            return this;
        }

        public ECSKinesisClientBuilder endPoint(String endPoint) throws URISyntaxException {
            if (checkValidate.apply(endPoint)) {
                throw new URISyntaxException(endPoint,
                        "invalid");
            }
            this.endPointUri = new URI(String.format("http://%s:4566",
                    endPoint));
            return this;
        }

        public ECSKinesisClientBuilder localStackEndPoint() throws URISyntaxException {
            String host = System.getenv("LOCALSTACK_HOSTNAME");
            return endPoint(host);
        }

        public ECSKinesisClient build() throws URISyntaxException {
            ECSKinesisClient client = new ECSKinesisClient();
            return client.create(this.region,
                    this.endPointUri);
        }
    }

    static java.util.function.Function<String, Boolean> checkValidate = x -> {
        final String HOST_PATTERN = "^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9-]*[a-zA-Z0-9])\\.)*" + "([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9-]*[A-Za-z0-9])$";

        final String PORT_PATTERN = "^\\d{1,5}$";

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
}
