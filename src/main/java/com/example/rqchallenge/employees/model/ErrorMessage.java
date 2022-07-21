package com.example.rqchallenge.employees.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class ErrorMessage {
    @JsonProperty
    private String error;

    public ErrorMessage(String error) {
        this.error = error;
    }
}
