package com.example.rqchallenge.employees.exception;

public class TooManyRequestsException extends RuntimeException {
    public TooManyRequestsException(String url) {
        super("APi request to url " + url + " got throttling response");
    }
}
