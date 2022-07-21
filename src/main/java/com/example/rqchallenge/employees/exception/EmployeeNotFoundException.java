package com.example.rqchallenge.employees.exception;

public class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException(String id) {
        super("Employee with id " + id + " not found");
    }
}
