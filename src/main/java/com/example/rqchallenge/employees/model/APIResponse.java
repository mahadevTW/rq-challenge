package com.example.rqchallenge.employees.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class APIResponse {
    @JsonProperty
    private String status;
    @JsonProperty
    private String message;
}
