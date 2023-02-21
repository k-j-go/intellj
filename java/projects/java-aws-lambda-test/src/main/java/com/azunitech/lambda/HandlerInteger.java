package com.azunitech.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

// Handler value: example.HandlerInteger
public class HandlerInteger implements RequestHandler<Integer, Integer> {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public Integer handleRequest(Integer event, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("EVENT: " + event + "\n");

        // process event
        logger.log("EVENT: " + gson.toJson(event) + "\n");
        logger.log("EVENT TYPE: " + event.getClass().toString() + "\n");
        // return amount of time remaining before timeout
        if (event.intValue() == 1) {
            throw new RuntimeException("ERROR INPUT");
        }

        int result = context.getRemainingTimeInMillis();
        logger.log("result: " + result + "\n");
        return result;
    }
}