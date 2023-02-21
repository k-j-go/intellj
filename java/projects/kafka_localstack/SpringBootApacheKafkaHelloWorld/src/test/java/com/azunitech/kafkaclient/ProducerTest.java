package com.azunitech.kafkaclient;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Test;

import java.util.Properties;
import java.util.stream.IntStream;

public class ProducerTest {
    @Test
    public void pilot() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("acks", "all");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        try (Producer<String, String> producer = new KafkaProducer<>(properties)) {
            IntStream.range(1, 5).forEach(i -> {
                producer.send(new ProducerRecord<>("my-topic", Integer.toString(i), "message-" + i));
            });
        }
    }
}
