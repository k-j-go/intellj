package com.azunitech.search;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.function.context.FunctionalSpringApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LambdaApp {

    private static Log logger = LogFactory.getLog(LambdaApp.class);

    public static void main(String[] args) {
        FunctionalSpringApplication.run(LambdaApp.class, args);
    }

    @Bean
    public ExampleFunction exampleFunction() {
        return new ExampleFunction();
    }

    @Bean
    public ExampleConsumer exampleConsumer() {
        return new ExampleConsumer();
    }

    @Bean
    public ExampleSupplier exampleSupplier() {
        return new ExampleSupplier();
    }
}