package com.example.rqchallenge.employees.service;

import com.example.rqchallenge.TestConfig;
import com.example.rqchallenge.employees.exception.EmployeeNotFoundException;
import com.example.rqchallenge.employees.exception.ExternalApiFailureException;
import com.example.rqchallenge.employees.exception.NoRecordFoundException;
import com.example.rqchallenge.employees.exception.TooManyRequestsException;
import com.example.rqchallenge.employees.model.CreateEmployeeExternalResponse;
import com.example.rqchallenge.employees.model.Employee;
import com.example.rqchallenge.employees.model.EmployeeResponse;
import com.example.rqchallenge.employees.model.EmployeesResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static com.example.rqchallenge.TestData.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
@SpringBootTest
class EmployeeServiceTest {
    @Autowired
    private EmployeeService employeeService;
    @MockBean
    RestTemplate restTemplate;

    @AfterEach
    public void cleanup() {
        Mockito.reset(restTemplate);
    }

    //TODO : assert on more data than just count
    @Test
    public void getAllEmployeeSuccess() throws JsonProcessingException {
        when(restTemplate.getForEntity("/employees", EmployeesResponse.class)).thenReturn(new ResponseEntity<>(sampleEmployeesResponse(), HttpStatus.OK));
        List<Employee> allEmployees = employeeService.getAllEmployees();
        assert allEmployees.size() == 3;
    }

    @Test
    public void getAllEmployeeFailsWhenExternalServiceFails() {
        String errorMessage = "server error";
        when(restTemplate.getForEntity("/employees", EmployeesResponse.class)).thenThrow(new RestClientException(errorMessage));

        ExternalApiFailureException exception = assertThrows(ExternalApiFailureException.class, () -> {
            employeeService.getAllEmployees();
        });

        assertEquals(exception.getMessage(), errorMessage);
    }

    @Test
    public void searchEmployeesReturnsMatchingEmployees() throws JsonProcessingException {
        when(restTemplate.getForEntity("/employees", EmployeesResponse.class))
                .thenReturn(new ResponseEntity<>(sampleEmployeesResponse(), HttpStatus.OK));
        List<Employee> allEmployees = employeeService.searchEmployees("Tiger");
        assertEquals(2, allEmployees.size());
        Employee employee1 = allEmployees.get(0);
        assertEquals(employee1.getId(), "1");
        assertEquals(employee1.getEmployeeName(), "Tiger Nixon");
        assertEquals(employee1.getEmployeeAge(), "61");
        assertEquals(employee1.getEmployeeSalary(), Integer.valueOf(320800));

        Employee employee2 = allEmployees.get(1);
        assertEquals(employee2.getId(), "23");
        assertEquals(employee2.getEmployeeName(), "Caesar Tiger Vance");
        assertEquals(employee2.getEmployeeAge(), "21");
        assertEquals(employee2.getEmployeeSalary(), Integer.valueOf(106450));
    }

    @Test
    public void searchEmployeesReturnsMatchingEmployeesReturnsEmptyResponse() throws JsonProcessingException {
        when(restTemplate.getForEntity("/employees", EmployeesResponse.class))
                .thenReturn(new ResponseEntity<>(sampleEmployeesResponse(), HttpStatus.OK));
        List<Employee> allEmployees = employeeService.searchEmployees("John");
        assertEquals(0, allEmployees.size());
    }

    @Test
    public void getEmployeeByIdShouldReturnMatchingEmployee() throws JsonProcessingException {
        when(restTemplate.getForEntity("/employee/1", EmployeeResponse.class)).thenReturn(new ResponseEntity<>(sampleEmployeeResponse(), HttpStatus.OK));
        Employee employee = employeeService.getEmployeeById("1");
        assertEquals(employee.getId(), "1");
        assertEquals(employee.getEmployeeName(), "Tiger Nixon");
        assertEquals(employee.getEmployeeAge(), "61");
        assertEquals(employee.getEmployeeSalary(), Integer.valueOf(320800));
    }

    @Test
    public void getEmployeeByIdShouldThrowEmployeeNotFoundException() {
        when(restTemplate.getForEntity("/employee/2", EmployeeResponse.class)).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
        assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.getEmployeeById("2");
        });
    }

    @Test
    public void getHighestSalaryShouldThrowRecordNotFoundExceptionIfNoDataFound() throws JsonProcessingException {
        when(restTemplate.getForEntity("/employees", EmployeesResponse.class)).thenReturn(new ResponseEntity<>(sampleEmptyEmployeeResponse(), HttpStatus.OK));
        List<Employee> allEmployees = employeeService.searchEmployees("John");
        assert allEmployees.size() == 0;
        assertThrows(NoRecordFoundException.class, () -> {
            employeeService.getHighestEmployeeSalary();
        });
    }

    @Test
    public void getHighestSalaryShouldReturnHighestSalary() throws JsonProcessingException {
        when(restTemplate.getForEntity("/employees", EmployeesResponse.class)).thenReturn(new ResponseEntity<>(sampleEmployeesResponse(), HttpStatus.OK));

        Integer highestSalary = employeeService.getHighestEmployeeSalary();
        assert 320800 == highestSalary;
    }

    @Test
    public void getTopNHighestEarningEmployeeNames() throws JsonProcessingException {
        when(restTemplate.getForEntity("/employees", EmployeesResponse.class)).thenReturn(new ResponseEntity<>(sampleEmployeesResponse(), HttpStatus.OK));

        List<String> topHighestEarningEmployeeNames = employeeService.getTopHighestEarningEmployeeNames(2);
        assertEquals("Tiger Nixon", topHighestEarningEmployeeNames.get(0));
        assertEquals("Caesar Tiger Vance", topHighestEarningEmployeeNames.get(1));
    }

    @Test
    public void createEmployeeThrowsTooManyRequestsException() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(createEmployeeRequestBody(), headers);


        when(restTemplate.postForEntity("/create", requestEntity, CreateEmployeeExternalResponse.class)).thenThrow(new HttpClientErrorException(HttpStatus.TOO_MANY_REQUESTS));
        TooManyRequestsException exception = assertThrows(TooManyRequestsException.class, () -> {
            employeeService.createEmployee(createEmployeeRequestBody());
        });
    }

    @Test
    public void createEmployeeThrowsFailed() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(createEmployeeRequestBody(), headers);


        when(restTemplate.postForEntity("/create", requestEntity, CreateEmployeeExternalResponse.class)).thenThrow(new HttpClientErrorException(HttpStatus.BAD_GATEWAY));
        ExternalApiFailureException exception = assertThrows(ExternalApiFailureException.class, () -> {
            employeeService.createEmployee(createEmployeeRequestBody());
        });
    }

    @Test
    public void createEmployeeSuccess() {
        CreateEmployeeExternalResponse expected = sampleCreateEmployeeResponse();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(createEmployeeRequestBody(), headers);

        when(restTemplate.postForEntity("/create", requestEntity, CreateEmployeeExternalResponse.class))
                .thenReturn(new ResponseEntity<>(expected, HttpStatus.OK));

        Employee employee = employeeService.createEmployee(createEmployeeRequestBody());

        assertEquals(expected.getName(), employee.getEmployeeName());
        assertEquals(expected.getAge(), employee.getEmployeeAge());
        assertEquals(expected.getId(), employee.getId());
        assertEquals(expected.getSalary(), employee.getEmployeeSalary());
    }


    @Test
    public void deleteEmployeeFails() {
        when(restTemplate.getForEntity("/delete/1", String.class)).thenThrow(new HttpClientErrorException(HttpStatus.BAD_GATEWAY));
        ExternalApiFailureException exception = assertThrows(ExternalApiFailureException.class, () -> {
            employeeService.deleteEmployee("1");
        });
    }

    @Test
    public void deleteEmployeeFailedWithNotFound() {
        when(restTemplate.getForEntity("/delete/1", String.class)).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.deleteEmployee("1");
        });
    }


    @Test
    public void deleteEmployeeSuccess() {
        when(restTemplate.getForEntity("/delete/1", String.class))
                .thenReturn(new ResponseEntity<>("successfully deleted employee", HttpStatus.OK));
        employeeService.deleteEmployee("1");
    }


}