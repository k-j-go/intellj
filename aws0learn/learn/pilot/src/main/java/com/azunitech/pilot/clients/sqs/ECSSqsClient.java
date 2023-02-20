package com.azunitech.pilot.clients.sqs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.regex.Pattern;

public class ECSSqsClient {
    private static final Logger logger = LoggerFactory.getLogger(ECSSqsClient.class);
    SqsClient sqsClient;

    public void create(Region region, URI endPointUri) throws URISyntaxException {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
                "local",
                "local");
        sqsClient = SqsClient.builder()
                .region(region)
                .endpointOverride(endPointUri)
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
            GetQueueUrlResponse getQueueUrlResponse = sqsClient.getQueueUrl(GetQueueUrlRequest.builder()
                    .queueName(queueName)
                    .build());
            return getQueueUrlResponse.queueUrl();

        } catch (SqsException e) {
            logger.error(e.awsErrorDetails()
                    .errorMessage());
        }
        return "";
    }

    public void listQueues() {
        // snippet-start:[sqs.java2.sqs_example.list_queues]
        String prefix = "que";

        try {
            ListQueuesRequest listQueuesRequest = ListQueuesRequest.builder()
                    .queueNamePrefix(prefix)
                    .build();
            ListQueuesResponse listQueuesResponse = sqsClient.listQueues(listQueuesRequest);
            for (String url : listQueuesResponse.queueUrls()) {
                logger.info(url);
            }

        } catch (SqsException e) {
            logger.error(e.awsErrorDetails()
                    .errorMessage());
        }
    }

    public void listQueuesFilter(String queueUrl) {
        // List queues with filters
        String namePrefix = "queue";
        ListQueuesRequest filterListRequest = ListQueuesRequest.builder()
                .queueNamePrefix(namePrefix)
                .build();

        ListQueuesResponse listQueuesFilteredResponse = sqsClient.listQueues(filterListRequest);
        logger.info("Queue URLs with prefix: " + namePrefix);
        for (String url : listQueuesFilteredResponse.queueUrls()) {
            logger.info(url);
        }

        logger.info("Send message");
        try {
            // snippet-start:[sqs.java2.sqs_example.send_message]
            sqsClient.sendMessage(SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody("Hello world!")
                    .delaySeconds(10)
                    .build());
        } catch (SqsException e) {
            logger.error(e.awsErrorDetails()
                    .errorMessage());
        }
    }

    public void sendBatchMessages(String queueUrl) {

        logger.info("Send multiple messages");
        try {
            // snippet-start:[sqs.java2.sqs_example.send__multiple_messages]
            SendMessageBatchRequest sendMessageBatchRequest = SendMessageBatchRequest.builder()
                    .queueUrl(queueUrl)
                    .entries(SendMessageBatchRequestEntry.builder()
                                    .id("id1")
                                    .messageBody("Hello from msg 1")
                                    .build(),
                            SendMessageBatchRequestEntry.builder()
                                    .id("id2")
                                    .messageBody("msg 2")
                                    .delaySeconds(10)
                                    .build())
                    .build();
            sqsClient.sendMessageBatch(sendMessageBatchRequest);
        } catch (SqsException e) {
            logger.error(e.awsErrorDetails()
                    .errorMessage());
        }
    }

    public List<Message> receiveMessages(String queueUrl) {

        logger.info("Receive messages");
        try {
            // snippet-start:[sqs.java2.sqs_example.retrieve_messages]
            ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .maxNumberOfMessages(5)
                    .build();
            return sqsClient.receiveMessage(receiveMessageRequest)
                    .messages();

        } catch (SqsException e) {
            logger.error(e.awsErrorDetails()
                    .errorMessage());
        }
        return null;
    }

    public void changeMessages(String queueUrl, List<Message> messages) {

        logger.info("Change Message Visibility");
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
            logger.error(e.awsErrorDetails()
                    .errorMessage());
        }
    }

    public void deleteMessages(String queueUrl, List<Message> messages) {
        logger.info("Delete Messages");

        try {
            for (Message message : messages) {
                DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .receiptHandle(message.receiptHandle())
                        .build();
                sqsClient.deleteMessage(deleteMessageRequest);
            }
        } catch (SqsException e) {
            logger.error(e.awsErrorDetails()
                    .errorMessage());
        }
    }

    public void close() {
        sqsClient.close();
    }

    public static ECSSqsClientBuilder builder() {
        return new ECSSqsClientBuilder();
    }

    public static class ECSSqsClientBuilder {

        private Region region;
        private URI endPointUri;

        public ECSSqsClientBuilder setRegion(Region region) {
            this.region = region;
            return this;
        }

        public ECSSqsClientBuilder setEndPoint(String endPoint) throws URISyntaxException {
            if (checkValidate.apply(endPoint)) {
                throw new URISyntaxException(endPoint, "invalid");
            }
            this.endPointUri = new URI(String.format("http://%s:4566", endPoint));
            return this;
        }

        public ECSSqsClientBuilder setLocalStackEndPoint() throws URISyntaxException {
            String host = System.getenv("LOCALSTACK_HOSTNAME");
            return setEndPoint(host);
        }

        public ECSSqsClient build() throws URISyntaxException {
            ECSSqsClient client = new ECSSqsClient();
            client.create(this.region, this.endPointUri);
            return client;
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
