package com.exampledev.demodev.example.exceptions;

public class EmptyResponseException extends RuntimeException {
    public EmptyResponseException(String message) {
        super(message);
    }
}