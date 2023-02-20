package com.azunitech.search.kinesis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.awssdk.services.kinesis.model.*;

import java.util.concurrent.CompletableFuture;

@Component
public class ECSAsyncKinesisClient {

    @Autowired
    ECSKinesisClient kinesisClient;

    public void create(String streamARN, String consumerName) {
        String consumer_agn = kinesisClient.regConsumer(streamARN, consumerName);

        KinesisAsyncClient client = KinesisAsyncClient.create();
        SubscribeToShardRequest request = SubscribeToShardRequest.builder()
                .consumerARN(consumer_agn)
                .overrideConfiguration(x -> {
                    AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
                            "local",
                            "local");
                    x.credentialsProvider( StaticCredentialsProvider.create(awsCreds));
                })
                .shardId("shardId-000000000000")
                .startingPosition(StartingPosition.builder().type(ShardIteratorType.LATEST).build())
                .build();

        responseHandlerBuilder_Reactor(client, request).join();

        client.close();
    }

    private CompletableFuture<Void> responseHandlerBuilder_Reactor(KinesisAsyncClient client, SubscribeToShardRequest request) {
        SubscribeToShardResponseHandler responseHandler = SubscribeToShardResponseHandler
                .builder()
                .onError(t -> System.err.println("Error during stream - " + t.getMessage()))
                .onEventStream(p -> Flux.from(p)
                        .ofType(SubscribeToShardEvent.class)
                        .flatMapIterable(SubscribeToShardEvent::records)
                        .limitRate(1000)
                        .buffer(25)
                        .subscribe(e -> System.out.println("Record batch = " + e)))
                .build();
        return client.subscribeToShard(request, responseHandler);
    }

    private static CompletableFuture<Void> responseHandlerBuilder_OnEventStream_Reactor(KinesisAsyncClient client, SubscribeToShardRequest request) {
        SubscribeToShardResponseHandler responseHandler = SubscribeToShardResponseHandler
                .builder()
                .onError(t -> System.err.println("Error during stream - " + t.getMessage()))
                .publisherTransformer(p -> Flux.from(p).limitRate(100).as(SdkPublisher::adapt))
                .build();
        return client.subscribeToShard(request, responseHandler);
    }
}
