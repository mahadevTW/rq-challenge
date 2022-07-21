package com.example.rqchallenge.employees.controller;

import com.example.rqchallenge.employees.exception.EmployeeNotFoundException;
import com.example.rqchallenge.employees.exception.ExternalApiFailureException;
import com.example.rqchallenge.employees.exception.TooManyRequestsException;
import com.example.rqchallenge.employees.model.Employee;
import com.example.rqchallenge.employees.model.ErrorMessage;
import com.example.rqchallenge.employees.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class EmployeeController implements IEmployeeController {

    @Autowired
    EmployeeService employeeService;

    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return new ResponseEntity<>(employeeService.getAllEmployees(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        return new ResponseEntity<>(employeeService.searchEmployees(searchString), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(String id) {
        return new ResponseEntity<>(employeeService.getEmployeeById(id), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        return new ResponseEntity<>(employeeService.getHighestEmployeeSalary(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        List<String> employeeNames = employeeService.getTopHighestEarningEmployeeNames(10);
        return new ResponseEntity<>(employeeNames, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Employee> createEmployee(Map<String, Object> employeeInput) {
        Employee employee = employeeService.createEmployee(employeeInput);
        return new ResponseEntity<>(employee, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {
        employeeService.deleteEmployee(id);
        return new ResponseEntity<>("successfully deleted employee", HttpStatus.OK);
    }

    @ExceptionHandler({TooManyRequestsException.class})
    public ResponseEntity<ErrorMessage> handleExceptionThrottlingError(TooManyRequestsException exception) {
        log.debug("exception handler , throttling error occured", exception);
        return new ResponseEntity<>(new ErrorMessage(exception.getMessage()), HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler({EmployeeNotFoundException.class})
    public ResponseEntity<ErrorMessage> handleExceptionEmployeeNotFound(EmployeeNotFoundException exception) {
        log.debug("exception handler , employee not found error", exception);
        return new ResponseEntity<>(new ErrorMessage(exception.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ExternalApiFailureException.class})
    public ResponseEntity<ErrorMessage> handleExternalApiFailureException(ExternalApiFailureException exception) {
        log.debug("External api call got failed", exception);
        return new ResponseEntity<>(new ErrorMessage("unknown error"), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
