package com.exampledev.demodev.example.service;

import com.exampledev.demodev.example.config.RetryProperties;
import com.exampledev.demodev.example.exceptions.EmptyResponseException;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;
import io.github.resilience4j.reactor.retry.RetryOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

@Slf4j
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
        myRetry1.getEventPublisher()
                .onRetry(event -> System.out.println("Retrying: " + event));
        myRetry1.getEventPublisher()
                .onRetry(event -> log.warn("Retrying [{}]: attempt #{}", event.getName(),
                        event.getNumberOfRetryAttempts()));
    }

    public Mono<String> getDataWithRetry1() {
        final int[] count = new int[] { 0 };
        logProperties("myRetry1");
        logger.info("csu before retry");

        Supplier<Mono<String>> supplier = () -> {
            log.info("csu before retry count: {}", count[0]++);
            return webClient.get()
                    .uri("http://localhost:5000/csuempty")
                    .retrieve()
                    .bodyToMono(String.class)
                    .flatMap(response -> {
                        if (response == null || response.isBlank()) {
                            return Mono.error(new EmptyResponseException("Empty response"));
                        }
                        return Mono.just(response);
                    });
        }; // Close the supplier block here

        return Retry.decorateSupplier(myRetry1, supplier).get();
    }

    public Mono<String> getDataWithRetry2() {
        final int[] count = new int[] { 0 };
        Supplier<Mono<String>> supplier = () -> {
            logProperties("myRetry2");
            log.info("myRetry2 retry count: {}", count[0]++);
            return webClient.get()
                    .uri("/")
                    .retrieve()
                    .bodyToMono(String.class);
        };

        return Retry.decorateSupplier(myRetry2, supplier).get();
    }

    @Bean(name = "testData")
    public void getTestDataDetails() {
        logProperties("myRetry1");
        logProperties("myRetry2");
    }

    private void logProperties(String retryName) {
        RetryProperties.RetryConfigProperties config = retryProperties.getInstances().get(retryName);
        logger.info("Retry Configuration test for {}: maxAttempts={}, waitDuration={}, isRetryEnable={}",
                retryName, config.getMaxAttempts(), config.getWaitDuration(), config.isRetryEnable());
    }
}