package com.exampledev.demodev.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exampledev.demodev.example.service.ApiService;

import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class ApiController {

    private final ApiService apiService;

    public ApiController(ApiService apiService) {
        this.apiService = apiService;
    }

    @GetMapping("/fetch-data-retry1")
    public Mono<String> fetchDataWithRetry1() {
        return apiService.getDataWithRetry1();
    }

    @GetMapping("/fetch-data-retry2")
    public Mono<String> fetchDataWithRetry2() {
        return apiService.getDataWithRetry2();
    }

    @GetMapping("/hi")
    public String sayHi() {
        return "hi, nice to see you";
    }

}