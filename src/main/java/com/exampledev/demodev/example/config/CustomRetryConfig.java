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
        RetryConfig myRetry1Config = createRetryConfig(MY_RETRY_1);
        RetryConfig myRetry2Config = createRetryConfig(MY_RETRY_2);

        RetryRegistry registry = RetryRegistry.of(myRetry1Config);
        registry.addConfiguration(MY_RETRY_2, myRetry2Config);
        return registry;
    }

    private RetryConfig createRetryConfig(String retryName) {
        int maxAttempts = retryProperties.getInstances().get(retryName).getMaxAttempts();
        return RetryConfig.custom()
                .maxAttempts(maxAttempts)
                .waitDuration(Duration.ofMillis(500)) // Base wait duration
                .retryExceptions(retryProperties.getInstances().get(retryName).getRetryExceptions()
                        .stream()
                        .map(this::getClassForName) // Convert string names to Class objects
                        .toArray(Class[]::new)) // Use Class[] to avoid type mismatch
                .build();
    }

    private Class<? extends Throwable> getClassForName(String className) {
        try {
            return (Class<? extends Throwable>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class not found: " + className, e);
        }
    }
}