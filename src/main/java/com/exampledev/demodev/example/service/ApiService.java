package com.exampledev.demodev.example.service;

import com.exampledev.demodev.example.config.RetryProperties;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.reactor.retry.RetryOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

@Service
public class ApiService {

    private static final Logger logger = LoggerFactory.getLogger(ApiService.class);

    private final WebClient webClient;
    private final Retry myRetry1;
    private final Retry myRetry2;
    private final RetryProperties retryProperties;

    public ApiService(RetryRegistry retryRegistry, RetryProperties retryProperties) {
        this.webClient = WebClient.builder().baseUrl("https://jsonplaceholder.typicode.com").build();
        this.myRetry1 = retryRegistry.retry("myRetry1");
        this.myRetry2 = retryRegistry.retry("myRetry2");
        this.retryProperties = retryProperties;
    }

    public Mono<String> getDataWithRetry1() {
        Supplier<Mono<String>> supplier = () -> {
            logProperties("myRetry1");
            return webClient.get()
                    .uri("/")
                    .retrieve()
                    .bodyToMono(String.class);
        };

        return retryProperties.getInstances().get("myRetry1").isRetryEnable()
                ? Mono.defer(supplier).transformDeferred(RetryOperator.of(myRetry1))
                : Mono.defer(supplier);
    }

    public Mono<String> getDataWithRetry2() {
        Supplier<Mono<String>> supplier = () -> {
            logProperties("myRetry2");
            return webClient.get()
                    .uri("/")
                    .retrieve()
                    .bodyToMono(String.class);
        };

        return retryProperties.getInstances().get("myRetry2").isRetryEnable()
                ? Mono.defer(supplier).transformDeferred(RetryOperator.of(myRetry2))
                : Mono.defer(supplier);
    }

    private void logProperties(String retryName) {
        RetryProperties.RetryConfigProperties config = retryProperties.getInstances().get(retryName);
        logger.info("Retry Configuration test for {}: maxAttempts={}, waitDuration={}, isRetryEnable={}",
                retryName, config.getMaxAttempts(), config.getWaitDuration(), config.isRetryEnable());
    }
}