package com.example.serverless;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.function.Function;

@Component
public class Hello implements Function<Flux<String>, Flux<String>> {
    @Override
    public Flux<String> apply(Flux<String> input) {
        return input.map(s -> s.toUpperCase());
    }
}
