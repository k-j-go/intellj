package com.azunitech.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.azunitech.lambda.domain.InProcessLambdaRequest;
import com.azunitech.lambda.domain.InProcessLambdaResponse;

public class HandlerFour implements RequestHandler<InProcessLambdaRequest, InProcessLambdaResponse> {
    @Override
    public InProcessLambdaResponse handleRequest(InProcessLambdaRequest input, Context context) {
        context.getLogger().log("Input: " + input);
        InProcessLambdaResponse lambdaResponse = new InProcessLambdaResponse();
        try {
            lambdaResponse.setPayload(String.format(input.getPayload() + " is handled by lamdba 4"));
            lambdaResponse.setTransactionId(input.getTransactionId());
            lambdaResponse.setStatus(input.getStatus());

        } catch (Exception e) {
            e.printStackTrace();
            lambdaResponse.setPayload(e.getMessage());
        }
        context.getLogger().log("Response : " + lambdaResponse);
        return lambdaResponse;
    }
}
