package com.azunitech.lambda.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LambdaMapInput {
    @JsonProperty("FunctionName")
    public String functionName;
    @JsonProperty("Payload")
    public Payload payload;
}
