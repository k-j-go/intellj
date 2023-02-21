package com.azunitech.search;

import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class ExampleConsumer implements Consumer<MyPojo> {

    @Override
    public void accept(MyPojo myPojo) {
        System.out.println("Received: " + myPojo.getExampleField());
    }
}