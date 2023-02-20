package com.azunitech.search.sqs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class SendReceiveMessages {
    private static final String QUEUE_NAME = "testQueue2";
    private SqsClient sqsClient;

    @BeforeEach
    public void setSQS() throws URISyntaxException {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
                "local",
                "local");
        Region region = Region.US_EAST_1;
        String endpoint = "http://127.0.0.1:4566";
        sqsClient = SqsClient.builder()
                .region(region)
                .endpointOverride(new URI(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();

    }

    @Test
    public void sendMessage() {
        CreateQueueRequest request = CreateQueueRequest.builder()
                .queueName(QUEUE_NAME)
                .build();
        CreateQueueResponse createResult = sqsClient.createQueue(request);

        GetQueueUrlRequest getQueueRequest = GetQueueUrlRequest.builder()
                .queueName(QUEUE_NAME)
                .build();

        String queueUrl = sqsClient.getQueueUrl(getQueueRequest).queueUrl();

        SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody("hello world")
                .delaySeconds(5)
                .build();
        sqsClient.sendMessage(sendMsgRequest);

        // Send multiple messages to the queue
        SendMessageBatchRequest sendBatchRequest = SendMessageBatchRequest.builder()
                .queueUrl(queueUrl)
                .entries(
                        SendMessageBatchRequestEntry.builder()
                                .messageBody("Hello from message 1")
                                .id("msg_1")
                                .build()
                        ,
                        SendMessageBatchRequestEntry.builder()
                                .messageBody("Hello from message 2")
                                .delaySeconds(10)
                                .id("msg_2")
                                .build())
                .build();
        sqsClient.sendMessageBatch(sendBatchRequest);
    }

    @Test
    public void receiveMessage() {
        GetQueueUrlRequest getQueueRequest = GetQueueUrlRequest.builder()
                .queueName(QUEUE_NAME)
                .build();

        String queueUrl = sqsClient.getQueueUrl(getQueueRequest).queueUrl();
        // Receive messages from the queue
        ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .build();
        List<Message> messages = sqsClient.receiveMessage(receiveRequest).messages();

        // Print out the messages
        for (Message m : messages) {
            System.out.println("\n" +m.body());
        }
    }

}
