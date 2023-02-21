package com.azunitech.search;

import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class ExampleSupplier implements Supplier<MyPojo> {

    @Override
    public MyPojo get() {
        return new MyPojo("Example Data");
    }
}