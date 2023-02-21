package com.azunitech.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.azunitech.lambda.domain.InLambdaRequest;
import com.azunitech.lambda.domain.InLambdaResponse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class HandlerOne implements RequestHandler<InLambdaRequest, InLambdaResponse> {
    @Override
    public InLambdaResponse handleRequest(InLambdaRequest input, Context context) {
        context.getLogger().log("Input: " + input);
        InLambdaResponse lambdaResponse = new InLambdaResponse();
        try {
            lambdaResponse.setPayload(String.format(input.getPayload() + " is handled by lamdba 1"));
            lambdaResponse.setTransactionId(UUID.randomUUID().toString());
            SimpleDateFormat formatter6 = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
            String dateInString = formatter6.format(new Date());
            lambdaResponse.setTime(dateInString);
        } catch (Exception e) {
            e.printStackTrace();
            lambdaResponse.setPayload(e.getMessage());
        }
        context.getLogger().log("Response : " + lambdaResponse);
        return lambdaResponse;
    }
}
