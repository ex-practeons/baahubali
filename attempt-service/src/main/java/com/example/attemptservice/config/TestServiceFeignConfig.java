package com.example.attemptservice.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class TestServiceFeignConfig {

    @Value("${internal.auth.my-secret}")
    private String mySecret;

    @Bean
    public RequestInterceptor testServiceRequestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("X-Service-Caller", "attempt-service");
            requestTemplate.header("X-Service-Auth", mySecret);
        };
    }
}