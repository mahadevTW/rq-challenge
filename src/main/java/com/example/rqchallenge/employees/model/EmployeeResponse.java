package com.example.rqchallenge.employees.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class EmployeeResponse extends APIResponse implements Serializable {
    @JsonProperty
    private Employee data;
}
