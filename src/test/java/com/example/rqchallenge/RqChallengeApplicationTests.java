package com.example.rqchallenge;

import com.example.rqchallenge.employees.controller.IEmployeeController;
import com.example.rqchallenge.employees.model.CreateEmployeeExternalResponse;
import com.example.rqchallenge.employees.model.Employee;
import com.example.rqchallenge.employees.model.EmployeeResponse;
import com.example.rqchallenge.employees.model.EmployeesResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static com.example.rqchallenge.TestData.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class RqChallengeApplicationTests {
    @Autowired
    private IEmployeeController employeeController;
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    RestTemplate restTemplate;
    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void contextLoads() {
        Assertions.assertThat(employeeController).isNotNull();
    }

    @Test
    public void shouldReturnReturnAllEmployees() throws Exception {
        when(restTemplate.getForEntity("/employees", EmployeesResponse.class))
                .thenReturn(new ResponseEntity<>(sampleEmployeesResponse(), HttpStatus.OK));

        String expectedJson = mapper.writeValueAsString(sampleEmployeesResponse().getData());

        this.mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void shouldReturnSpecificEmployee() throws Exception {
        when(restTemplate.getForEntity("/employee/1", EmployeeResponse.class))
                .thenReturn(new ResponseEntity<>(sampleEmployeeResponse(), HttpStatus.OK));

        String expectedJson = mapper.writeValueAsString(sampleEmployeeResponse().getData());

        this.mockMvc.perform(get("/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void shouldReturnHighestSalary() throws Exception {
        when(restTemplate.getForEntity("/employees", EmployeesResponse.class))
                .thenReturn(new ResponseEntity<>(sampleEmployeesResponse(), HttpStatus.OK));

        this.mockMvc.perform(get("/highestSalary"))
                .andExpect(status().isOk())
                .andExpect(content().string("320800"));
    }

    @Test
    public void shouldReturnTopTenHighestEarningEmployeeNames() throws Exception {
        when(restTemplate.getForEntity("/employees", EmployeesResponse.class))
                .thenReturn(new ResponseEntity<>(sampleEmployeesResponse(), HttpStatus.OK));

        this.mockMvc.perform(get("/topTenHighestEarningEmployeeNames"))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"Tiger Nixon\",\"Caesar Tiger Vance\",\"Doris Wilder\"]"));
    }

    @Test
    public void shouldCreateEmployee() throws Exception {

        Map<String, Object> employeeRequestBody = createEmployeeRequestBody();
        String request = mapper.writeValueAsString(employeeRequestBody);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(employeeRequestBody, headers);

        when(restTemplate.postForEntity("/create", requestEntity, CreateEmployeeExternalResponse.class))
                .thenReturn(new ResponseEntity<>(sampleCreateEmployeeResponse(), HttpStatus.CREATED));

        String expectedResponse = mapper.writeValueAsString(new Employee(
                "25",
                employeeRequestBody.get("name").toString(),
                Integer.parseInt(employeeRequestBody.get("salary").toString()),
                employeeRequestBody.get("age").toString(), ""));

        this.mockMvc.perform(post("/")
                        .content(request)
                        .contentType("application/json"))
                .andExpect(status().isCreated())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    public void shouldDeleteEmployee() throws Exception {
        when(restTemplate.getForEntity("/delete/1", String.class))
                .thenReturn(new ResponseEntity<>("successfully deleted employee", HttpStatus.OK));


        this.mockMvc.perform(delete("/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("successfully deleted employee"));
    }
}