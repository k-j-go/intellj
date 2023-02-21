package com.azunitech.search;


import lombok.Data;

@Data
public class MyPojo {

    private String exampleField;

    public MyPojo() { }

    public MyPojo(String field) {
        this.exampleField = field;
    }
}
