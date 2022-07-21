package com.example.rqchallenge.employees.service;

import com.example.rqchallenge.employees.exception.EmployeeNotFoundException;
import com.example.rqchallenge.employees.exception.ExternalApiFailureException;
import com.example.rqchallenge.employees.exception.NoRecordFoundException;
import com.example.rqchallenge.employees.exception.TooManyRequestsException;
import com.example.rqchallenge.employees.model.CreateEmployeeExternalResponse;
import com.example.rqchallenge.employees.model.Employee;
import com.example.rqchallenge.employees.model.EmployeeResponse;
import com.example.rqchallenge.employees.model.EmployeesResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;

@Service
@Slf4j
public class EmployeeService {

    public static final String GET_ALL_EMPLOYEES_URL = "/employees";
    public static final String GET_EMPLOYEE_BY_ID_URL = "/employee/%s";
    public static final String DELETE_EMPLOYEE_URL = "/delete/%s";
    public static final String CREATE_EMPLOYEE_URL = "/create";

    @Autowired
    private RestTemplate restTemplate;

    public List<Employee> getAllEmployees() {
        return fetchEmployees(Optional.empty(), Optional.empty(), Optional.empty());
    }

    public List<Employee> searchEmployees(String searchString) {
        Predicate<Employee> filterPredicate = e -> e.getEmployeeName().toLowerCase().contains(searchString.toLowerCase());
        return fetchEmployees(Optional.empty(), Optional.of(filterPredicate), Optional.empty());
    }

    public Employee getEmployeeById(String id) {
        ResponseEntity<EmployeeResponse> response;
        try {
            response = restTemplate.getForEntity(format(GET_EMPLOYEE_BY_ID_URL, id), EmployeeResponse.class);
        } catch (HttpStatusCodeException e) {
            log.error("failed to fetch fetch employee information , error :", e);
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.debug("employee with id {} not found. error : {}", id, e);
                throw new EmployeeNotFoundException(id);
            }
            throw new ExternalApiFailureException(e.getMessage());
        }
        return response.getBody().getData();
    }

    public Integer getHighestEmployeeSalary() {
        Comparator<Employee> sortBySalary = (e1, e2) -> e2.getEmployeeSalary() - e1.getEmployeeSalary();
        List<Employee> employees = fetchEmployees(Optional.of(sortBySalary), Optional.empty(), Optional.of(1));
        if (employees.isEmpty()) {
            throw new NoRecordFoundException("No employee record found");
        }
        return employees.get(0).getEmployeeSalary();
    }


    public Employee createEmployee(Map<String, Object> employeeInput) {
        ResponseEntity<CreateEmployeeExternalResponse> employeeResponseEntity = null;
        try {
            HttpEntity<Map<String, Object>> requestEntity = toCreateEmployeeHttpRequest(employeeInput);
            employeeResponseEntity = restTemplate.postForEntity(CREATE_EMPLOYEE_URL, requestEntity, CreateEmployeeExternalResponse.class);
        } catch (HttpStatusCodeException e) {
            handleThrottlingException(e);
        } catch (Exception e) {
            log.error("failed to create employee ", e);
            throw new ExternalApiFailureException("failed to create employee");
        }
        CreateEmployeeExternalResponse body = employeeResponseEntity.getBody();
        return new Employee(body.getId(), body.getName(), body.getSalary(), body.getAge(), "");
    }

    private HttpEntity<Map<String, Object>> toCreateEmployeeHttpRequest(Map<String, Object> employeeInput) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(employeeInput, headers);
        return requestEntity;
    }

    private void handleThrottlingException(HttpStatusCodeException e) {
        if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
            log.error("external service throttling request with error ", e);
            throw new TooManyRequestsException(CREATE_EMPLOYEE_URL);
        }
        log.error("failed to create employee ", e);
        throw new ExternalApiFailureException("failed to create employee");
    }

    public List<String> getTopHighestEarningEmployeeNames(int count) {
        Comparator<Employee> sortBySalary = (e1, e2) -> e2.getEmployeeSalary() - e1.getEmployeeSalary();
        List<Employee> employees = fetchEmployees(Optional.of(sortBySalary), Optional.empty(), Optional.of(count));
        return employees.stream().map(Employee::getEmployeeName).collect(Collectors.toList());
    }

    public void deleteEmployee(String id) {
        try {
            restTemplate.getForEntity(format(DELETE_EMPLOYEE_URL, id), String.class);
        } catch (HttpStatusCodeException e) {
            log.debug("failed to delete employee with error", e);
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new EmployeeNotFoundException(id);
            }
            throw new ExternalApiFailureException("failed to delete employee with id " + id);
        } catch (Exception e) {
            log.debug("failed to delete employee with error", e);
            throw new ExternalApiFailureException("failed to delete employee with id " + id);
        }
    }

    private List<Employee> fetchEmployees(Optional<Comparator<Employee>> orderBy, Optional<Predicate<Employee>> filter, Optional<Integer> top) {
        ResponseEntity<EmployeesResponse> response = null;
        try {
            response = restTemplate.getForEntity(GET_ALL_EMPLOYEES_URL, EmployeesResponse.class);
        } catch (HttpStatusCodeException e) {
            handleThrottlingException(e);
        } catch (Exception e) {
            log.error("failed to fetch employee information from external api , error :", e);
            throw new ExternalApiFailureException(e.getMessage());
        }
        Stream<Employee> employeesStream = response.getBody().getData().stream();
        if (orderBy.isPresent()) {
            employeesStream = employeesStream.sorted(orderBy.get());
        }
        if (filter.isPresent()) {
            employeesStream = employeesStream.filter(filter.get());
        }
        if (top.isPresent()) {
            employeesStream = employeesStream.limit(top.get());
        }
        List<Employee> employees = employeesStream.collect(Collectors.toList());
        log.info("received employee information from dummy service, employee count : {}", employees.size());
        return employees;
    }
}
