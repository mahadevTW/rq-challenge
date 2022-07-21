package com.example.rqchallenge.employees.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class CreateEmployeeExternalResponse implements Serializable {
    @JsonProperty
    private String id;
    @JsonProperty()
    private String name;
    @JsonProperty()
    private Integer salary;
    @JsonProperty()
    private String age;
}

