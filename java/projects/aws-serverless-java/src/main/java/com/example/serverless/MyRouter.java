package com.example.serverless;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.time.LocalDateTime;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@Configuration
public class MyRouter {
    @Bean
    public RouterFunction<ServerResponse> routes() {
        return RouterFunctions
                .route(GET("/api/status"), request -> ServerResponse.ok().contentType(MediaType.TEXT_PLAIN).syncBody("status is up"))
                .andRoute(GET("/api/time"), request -> ServerResponse.ok().body(BodyInserters.fromObject(SampleResp.builder().localDateTime(LocalDateTime.now()).status("up").build())))
                ;

    }
}

