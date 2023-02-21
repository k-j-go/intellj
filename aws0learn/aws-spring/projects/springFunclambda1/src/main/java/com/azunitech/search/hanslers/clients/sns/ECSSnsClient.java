package com.azunitech.search.hanslers.clients.sns;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsAsyncClient;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

public class ECSSnsClient {
    private static final Logger logger = LoggerFactory.getLogger(ECSSnsClient.class);

    private SnsClient snsClient;
    private SnsAsyncClient snsAsyncClient;

    public void create(Region region, URI endPointUri) throws URISyntaxException {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
                "local",
                "local");
        snsClient = SnsClient.builder()
                .region(region)
                .endpointOverride(endPointUri)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .httpClient(UrlConnectionHttpClient.builder().build())
                .build();

//        snsAsyncClient = SnsAsyncClient.builder()
//                .region(region)
//                .endpointOverride(endPointUri)
//                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
//                .build();
    }

    public String createTopic(String topicName) {
        CreateTopicResponse result = null;
        try {
            CreateTopicRequest request = CreateTopicRequest.builder()
                    .name(topicName)
                    .build();

            result = snsClient.createTopic(request);
            return result.topicArn();

        } catch (SnsException e) {
            logger.error(e.awsErrorDetails()
                    .errorMessage());
        }
        return "";
    }

    public void pubTopic(String message, String topicArn) {

        try {
            PublishRequest request = PublishRequest.builder()
                    .message(message)
                    .topicArn(topicArn)
                    .build();

            PublishResponse result = snsClient.publish(request);
            System.out.println(result.messageId() + " Message sent. Status is " + result.sdkHttpResponse()
                    .statusCode());

        } catch (SnsException e) {
            logger.error(e.awsErrorDetails()
                    .errorMessage());
        }
    }

    public void listSNSTopics() {

        try {
            ListTopicsRequest request = ListTopicsRequest.builder()
                    .build();

            ListTopicsResponse result = snsClient.listTopics(request);
            System.out.println("Status was " + result.sdkHttpResponse()
                    .statusCode() + "\n\nTopics\n\n" + result.topics());

        } catch (SnsException e) {
            logger.error(e.awsErrorDetails()
                    .errorMessage());
        }
    }

    public boolean checkSNSTopicExists(String topicName) {
        try {
            ListTopicsRequest request = ListTopicsRequest.builder()
                    .build();

            ListTopicsResponse result = snsClient.listTopics(request);
            return result.topics()
                    .stream()
                    .filter(x -> x.topicArn()
                            .endsWith(String.format(":%s", topicName)))
                    .count() != 0;
        } catch (SnsException e) {
            logger.error(e.awsErrorDetails()
                    .errorMessage());
        }
        return true;
    }

    public void getSnsMessage() {
    }

    public static ECSSnsClientBuilder builder() {
        return new ECSSnsClientBuilder();
    }

    public static class ECSSnsClientBuilder {

        private Region region;
        private URI endPointUri;

        public ECSSnsClientBuilder region(Region region) {
            this.region = region;
            return this;
        }

        public ECSSnsClientBuilder host(String endPoint) throws URISyntaxException {
            if (checkValidate.apply(endPoint)) {
                throw new URISyntaxException(endPoint, "invalid");
            }
            this.endPointUri = new URI(String.format("http://%s:4566", endPoint));
            return this;
        }

        public ECSSnsClientBuilder localStackEndPoint() throws URISyntaxException {
            String host = System.getenv("LOCALSTACK_HOSTNAME");
            return host(host);
        }

        public ECSSnsClient build() throws URISyntaxException {
            ECSSnsClient client = new ECSSnsClient();
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
