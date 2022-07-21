package com.example.rqchallenge.employees.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
public class EmployeesResponse extends APIResponse implements Serializable {
    @JsonProperty
    private List<Employee> data;
}
