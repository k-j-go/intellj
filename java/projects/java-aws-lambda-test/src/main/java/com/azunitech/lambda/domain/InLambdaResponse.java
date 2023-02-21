package com.azunitech.lambda.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class InLambdaResponse implements Serializable, Cloneable{
    @JsonProperty("payload")
    String payload;

    @JsonProperty("status")
    int status;

    @JsonProperty("transactionId")
    String transactionId;

    @JsonProperty("time")
    String time;
}
