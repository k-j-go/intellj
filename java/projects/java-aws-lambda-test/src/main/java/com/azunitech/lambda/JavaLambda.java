package com.azunitech.lambda;

import com.amazonaws.lambda.thirdparty.com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.amazonaws.lambda.thirdparty.com.google.gson.Gson;
import com.amazonaws.lambda.thirdparty.com.google.gson.GsonBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.azunitech.lambda.domain.LambdaMapInput;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
    {
        "FunctionName": "JavaLambda",
        "Payload": {
                    "input": {
                                "Comment": "Insert your JSON here\"
                             }
                  }
    }
 */

public class JavaLambda implements RequestHandler<Map<String, Object>, String> {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public String handleRequest(Map<String, Object> event, Context context) {
        LambdaLogger logger = context.getLogger();
        String response = new String("200 OK");
        // log execution details
        logger.log("ENVIRONMENT VARIABLES: " + gson.toJson(System.getenv()));
        logger.log("CONTEXT: " + gson.toJson(context));
        // process event
        logger.log("EVENT: " + gson.toJson(event));
        logger.log("EVENT TYPE: " + event.getClass().toString());

        String json = gson.toJson(event);
        ObjectMapper mapper = new ObjectMapper();
        try {
            LambdaMapInput input = mapper.readValue(json, LambdaMapInput.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return response;
    }
}