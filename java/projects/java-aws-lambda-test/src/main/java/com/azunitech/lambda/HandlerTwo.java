package com.azunitech.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.azunitech.lambda.domain.InProcessLambdaResponse;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HandlerTwo implements RequestHandler<InProcessLambdaResponse, InProcessLambdaResponse> {

    @Override
    public InProcessLambdaResponse handleRequest(InProcessLambdaResponse input, Context context) {
        context.getLogger().log("Input: " + input);
        InProcessLambdaResponse lambdaResponse = new InProcessLambdaResponse();
        try {

            lambdaResponse.setTransactionId(input.getTransactionId());
            int max = 100;
            int min = 1;
            int range = max - min + 1;
            int rand = (int) (Math.random() * range) + min;
            lambdaResponse.setStatus(rand);
            lambdaResponse.setPayload(String.format(input.getPayload() + " is handled by lamdba 2"));
            lambdaResponse.setTime((new Date()).toString());
        } catch (Exception e) {
            e.printStackTrace();
            lambdaResponse.setPayload(e.getMessage());
        }
        context.getLogger().log("Response : " + lambdaResponse);
        return lambdaResponse;
    }
}
