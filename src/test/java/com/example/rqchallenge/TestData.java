package com.example.rqchallenge;

import com.example.rqchallenge.employees.model.CreateEmployeeExternalResponse;
import com.example.rqchallenge.employees.model.EmployeeResponse;
import com.example.rqchallenge.employees.model.EmployeesResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class TestData {
    public static CreateEmployeeExternalResponse sampleCreateEmployeeResponse() {
        return new CreateEmployeeExternalResponse("25", "test", 2000, "25");
    }

    public static EmployeesResponse sampleEmployeesResponse() throws JsonProcessingException {
        String json = "{\n" + "  \"status\": \"success\",\n" + "  \"data\": [\n" + "    {\n" + "      \"id\": 1,\n" + "      \"employee_name\": \"Tiger Nixon\",\n" + "      \"employee_salary\": 320800,\n" + "      \"employee_age\": 61,\n" + "      \"profile_image\": \"\"\n" + "    },\n" + "    {\n" + "      \"id\": 23,\n" + "      \"employee_name\": \"Caesar Tiger Vance\",\n" + "      \"employee_salary\": 106450,\n" + "      \"employee_age\": 21,\n" + "      \"profile_image\": \"\"\n" + "    },\n" + "    {\n" + "      \"id\": 24,\n" + "      \"employee_name\": \"Doris Wilder\",\n" + "      \"employee_salary\": 85600,\n" + "      \"employee_age\": 23,\n" + "      \"profile_image\": \"\"\n" + "    }\n" + "  ],\n" + "  \"message\": \"Successfully! All records has been fetched.\"\n" + "}";
        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(json, EmployeesResponse.class);
    }

    public static EmployeesResponse sampleEmptyEmployeeResponse() throws JsonProcessingException {
        String json = "{\n" + "  \"status\": \"success\",\n" + "  \"data\": [],\n" + "  \"message\": \"Successfully! All records has been fetched.\"\n" + "}";
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, EmployeesResponse.class);
    }

    public static EmployeeResponse sampleEmployeeResponse() throws JsonProcessingException {
        String json = "{\n" + "\"status\": \"success\",\n" + "\"data\": {\n" + "\"id\": \"1\",\n" + "\"employee_name\": \"Tiger Nixon\",\n" + "\"employee_salary\": \"320800\",\n" + "\"employee_age\": \"61\",\n" + "\"profile_image\": \"\"\n" + "}\n" + "}";
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, EmployeeResponse.class);
    }

    public static Map<String, Object> createEmployeeRequestBody() {
        HashMap<String, Object> employeeDetails = new HashMap<>();
        employeeDetails.put("name", "test");
        employeeDetails.put("salary", "2000");
        employeeDetails.put("age", "25");
        return employeeDetails;
    }
}
