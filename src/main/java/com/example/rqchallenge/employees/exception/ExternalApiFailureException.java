package com.example.rqchallenge.employees.exception;

public class ExternalApiFailureException extends RuntimeException {
    public ExternalApiFailureException(String message) {
        super(message);
    }
}
