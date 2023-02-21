package com.azunitech.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.function.Function;


@SpringBootApplication
public class SpringFunclambdaApplication {
    private static final Logger logger = LoggerFactory.getLogger(SpringFunclambdaApplication.class);

    public static void main(String[] args) {
        // empty unless using Custom runtime at which point it should include
        //SpringApplication.run(SpringFunclambdaApplication.class, args);
    }

    @Bean
    public Function<String, String> uppercase() {
        return value -> {
            logger.info(value);
            if (value.equals("exception")) {
                throw new RuntimeException("Intentional exception");
            } else {
                return value.toUpperCase();
            }
        };
    }
}

