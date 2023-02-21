package com.azunitech.lambda.statemachine;


import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.internal.config.EndpointDiscoveryConfig;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.*;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;

public class LambdaFunctionSDKTest {
    @Test
    public void listFunction() {
        // snippet-start:[lambda.java1.list.main]
        ListFunctionsResult functionResult = null;

        try {
            AwsClientBuilder.EndpointConfiguration ep = new AwsClientBuilder.EndpointConfiguration(
                    "http://127.0.0.1:4566", Regions.EU_WEST_2.toString());

            AWSLambda awsLambda = AWSLambdaClientBuilder.standard()
                    //.withCredentials(new ProfileCredentialsProvider())
                    //.withRegion(Regions.US_WEST_2)
                    .withEndpointConfiguration(ep).build();

            functionResult = awsLambda.listFunctions();

            List<FunctionConfiguration> list = functionResult.getFunctions();

            for (Iterator iter = list.iterator(); iter.hasNext(); ) {
                FunctionConfiguration config = (FunctionConfiguration) iter.next();
                System.out.println("The function name is " + config.getFunctionName());
            }

        } catch (ServiceException e) {
            System.out.println(e);
        } catch (Exception e) {
            System.out.println(e);

        }
    }

    @Test
    public void deleteFunction() {
        String functionName = "JavaLambda";
        try {
            AwsClientBuilder.EndpointConfiguration ep = new AwsClientBuilder.EndpointConfiguration(
                    "http://127.0.0.1:4566", Regions.EU_WEST_2.toString());

            AWSLambda awsLambda = AWSLambdaClientBuilder.standard()
                    .withCredentials(new ProfileCredentialsProvider())
                    //.withRegion(Regions.US_WEST_2).build();
                    .withEndpointConfiguration(ep).build();

            DeleteFunctionRequest delFunc = new DeleteFunctionRequest();
            delFunc.withFunctionName(functionName);

            //Delete the function
            awsLambda.deleteFunction(delFunc);
            System.out.println(String.format("The function $s is deleted", functionName));

        } catch (ServiceException e) {
            System.out.println(e);
        } catch (ResourceNotFoundException rne) {
            System.out.println(rne);
        }
    }

    @Test
    public void invodeFunction() {
        String functionName = "HandlerInteger";

        InvokeRequest invokeRequest = new InvokeRequest()
                .withFunctionName(functionName)
                .withPayload("1");
        InvokeResult invokeResult = null;

        try {
            AwsClientBuilder.EndpointConfiguration ep = new AwsClientBuilder.EndpointConfiguration(
                    "http://127.0.0.1:4566", Regions.EU_WEST_2.toString());

            AWSLambda awsLambda = AWSLambdaClientBuilder.standard()
                    .withCredentials(new ProfileCredentialsProvider())
                    //.withRegion(Regions.US_WEST_2).build();
                    .withEndpointConfiguration(ep).build();

            invokeResult = awsLambda.invoke(invokeRequest);
            String error = invokeResult.getFunctionError();
            String result = invokeResult.getLogResult();
            byte[] array = invokeResult.getPayload().array();

            String ans = new String(invokeResult.getPayload().array(), StandardCharsets.UTF_8);

            //write out the return value
            System.out.println(ans);

        } catch (ServiceException e) {
            System.out.println(e);
        }

        System.out.println(invokeResult.getStatusCode());
    }
}
