package com.azunitech.search;

import com.github.javafaker.Faker;
import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Locale;

@Configuration
@Import({})
public class Wire {
    @Bean
    FakeValuesService getFakeValuesService(){
        return new FakeValuesService(new Locale("en-GB"), new RandomService());
    }

    @Bean
    Faker getFaker(){
        return new Faker();
    }

}
