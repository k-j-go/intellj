package com.javainuse.services;

import org.springframework.stereotype.Service;
import org.springframework.kafka.annotation.KafkaListener;
@Service
public class KafkaListenerService {

    @KafkaListener(topics = "my-topic")
    public void listen(String message) {
        System.out.println("Received message: " + message);
    }
}
