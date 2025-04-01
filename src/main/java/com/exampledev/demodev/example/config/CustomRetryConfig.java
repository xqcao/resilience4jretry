package com.exampledev.demodev.example.config;

import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CustomRetryConfig {

    private final RetryProperties retryProperties;
    private static final String MY_RETRY_1 = "myRetry1";
    private static final String MY_RETRY_2 = "myRetry2";

    public CustomRetryConfig(RetryProperties retryProperties) {
        this.retryProperties = retryProperties;
    }

    @Bean
    public RetryRegistry retryRegistry() {
        RetryConfig myRetry1Config = RetryConfig.custom()
                .maxAttempts(retryProperties.getInstances().get(MY_RETRY_1).getMaxAttempts())
                .waitDuration(Duration.ofSeconds(retryProperties.getInstances().get(MY_RETRY_1).getWaitDuration()))
                .build();

        RetryConfig myRetry2Config = RetryConfig.custom()
                .maxAttempts(retryProperties.getInstances().get(MY_RETRY_2).getMaxAttempts())
                .waitDuration(Duration.ofSeconds(retryProperties.getInstances().get(MY_RETRY_2).getWaitDuration()))
                .build();

        RetryRegistry registry = RetryRegistry.of(myRetry1Config);
        registry.addConfiguration(MY_RETRY_2, myRetry2Config);
        return registry;
    }
}