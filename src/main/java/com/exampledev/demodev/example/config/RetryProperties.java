package com.exampledev.demodev.example.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "resilience4j.retry")
@Getter
@Setter
public class RetryProperties {

    private Map<String, RetryConfigProperties> instances;

    public Map<String, RetryConfigProperties> getInstances() {
        return instances;
    }

    public void setInstances(Map<String, RetryConfigProperties> instances) {
        this.instances = instances;
    }

    @Getter
    @Setter
    public static class RetryConfigProperties {
        private int maxAttempts;
        private int waitDuration;
        private boolean retryEnable;
        private List<String> retryExceptions;
    }
}