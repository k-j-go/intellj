package com.azunitech.search.sqs;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Component
@Log4j2
public class ECSSqsClient {
    @Value("${localstack.region:us-east-1}")
    private String region;

    @Value("${localstack.endpoint: http://127.0.0.1:4566}")
    private String endpoint;

    SqsClient sqsClient;

    @PostConstruct
    public void create() throws URISyntaxException {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
                "local",
                "local");
        Region region = Region.US_EAST_1;
        sqsClient = SqsClient.builder()
                .region(region)
                .endpointOverride(new URI(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }

    public String createQueue(String queueName) {

        try {
            CreateQueueRequest createQueueRequest = CreateQueueRequest.builder()
                    .queueName(queueName)
                    .build();

            sqsClient.createQueue(createQueueRequest);

            // snippet-start:[sqs.java2.sqs_example.get_queue]
            GetQueueUrlResponse getQueueUrlResponse = sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build());
            return getQueueUrlResponse.queueUrl();

        } catch (SqsException e) {
            log.error(e.awsErrorDetails().errorMessage());
        }
        return "";
    }

    public void listQueues() {
        // snippet-start:[sqs.java2.sqs_example.list_queues]
        String prefix = "que";

        try {
            ListQueuesRequest listQueuesRequest = ListQueuesRequest.builder().queueNamePrefix(prefix).build();
            ListQueuesResponse listQueuesResponse = sqsClient.listQueues(listQueuesRequest);
            for (String url : listQueuesResponse.queueUrls()) {
                log.info(url);
            }

        } catch (SqsException e) {
            log.error(e.awsErrorDetails().errorMessage());
        }
    }

    public void listQueuesFilter(String queueUrl) {
        // List queues with filters
        String namePrefix = "queue";
        ListQueuesRequest filterListRequest = ListQueuesRequest.builder()
                .queueNamePrefix(namePrefix)
                .build();

        ListQueuesResponse listQueuesFilteredResponse = sqsClient.listQueues(filterListRequest);
        log.info("Queue URLs with prefix: " + namePrefix);
        for (String url : listQueuesFilteredResponse.queueUrls()) {
            log.info(url);
        }

        log.info("Send message");
        try {
            // snippet-start:[sqs.java2.sqs_example.send_message]
            sqsClient.sendMessage(SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody("Hello world!")
                    .delaySeconds(10)
                    .build());
        } catch (SqsException e) {
            log.error(e.awsErrorDetails().errorMessage());
        }
    }

    public void sendBatchMessages(String queueUrl) {

        log.info("Send multiple messages");
        try {
            // snippet-start:[sqs.java2.sqs_example.send__multiple_messages]
            SendMessageBatchRequest sendMessageBatchRequest = SendMessageBatchRequest.builder()
                    .queueUrl(queueUrl)
                    .entries(SendMessageBatchRequestEntry.builder().id("id1").messageBody("Hello from msg 1").build(),
                            SendMessageBatchRequestEntry.builder().id("id2").messageBody("msg 2").delaySeconds(10).build())
                    .build();
            sqsClient.sendMessageBatch(sendMessageBatchRequest);
            // snippet-end:[sqs.java2.sqs_example.send__multiple_messages]

        } catch (SqsException e) {
            log.error(e.awsErrorDetails().errorMessage());
        }
    }

    public List<Message> receiveMessages(String queueUrl) {

        log.info("Receive messages");
        try {
            // snippet-start:[sqs.java2.sqs_example.retrieve_messages]
            ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .maxNumberOfMessages(5)
                    .build();
            return sqsClient.receiveMessage(receiveMessageRequest).messages();

        } catch (SqsException e) {
            log.error(e.awsErrorDetails().errorMessage());
        }
        return null;
    }

    public void changeMessages(String queueUrl, List<Message> messages) {

        log.info("Change Message Visibility");
        try {

            for (Message message : messages) {
                ChangeMessageVisibilityRequest req = ChangeMessageVisibilityRequest.builder()
                        .queueUrl(queueUrl)
                        .receiptHandle(message.receiptHandle())
                        .visibilityTimeout(100)
                        .build();
                sqsClient.changeMessageVisibility(req);
            }

        } catch (SqsException e) {
            log.error(e.awsErrorDetails().errorMessage());
        }
    }

    public void deleteMessages(String queueUrl, List<Message> messages) {
        log.info("Delete Messages");

        try {
            for (Message message : messages) {
                DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .receiptHandle(message.receiptHandle())
                        .build();
                sqsClient.deleteMessage(deleteMessageRequest);
            }
        } catch (SqsException e) {
            log.error(e.awsErrorDetails().errorMessage());
        }
    }

    public void close() {
        sqsClient.close();
    }
}
