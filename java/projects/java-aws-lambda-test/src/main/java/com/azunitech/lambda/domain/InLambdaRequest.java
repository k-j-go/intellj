package com.azunitech.lambda.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class InLambdaRequest implements Serializable, Cloneable{
    @JsonProperty("payload")
    private String payload;
}
