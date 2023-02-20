package com.azunitech.search.kinesis;

import com.azunitech.search.kinesis.models.Address;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.*;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ECSKinesisClient {
    @Value("${localstack.region:us-east-1}")
    private String region;

    @Value("${localstack.endpoint: http://127.0.0.1:4566}")
    private String endpoint;

    KinesisClient kinesisClient;

    @PostConstruct
    public void create() throws URISyntaxException {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
                "local",
                "local");
        Region region = Region.US_EAST_1;
        kinesisClient = KinesisClient.builder()
                .region(region)
                .endpointOverride(new URI(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
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

            if (!describeStreamResponse.streamDescription().streamStatus().toString().equals("ACTIVE")) {
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
            shards.addAll(streamRes.streamDescription().shards());

            if (shards.size() > 0) {
                lastShardId = shards.get(shards.size() - 1).shardId();
            }
        } while (streamRes.streamDescription().hasMoreShards());

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
            System.out.printf("Seq No: %s - %s%n", record.sequenceNumber(), new String(byteBuffer.asByteArray()));
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
            return resp.consumer().consumerARN();

        } catch (KinesisException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return "";
    }
}
