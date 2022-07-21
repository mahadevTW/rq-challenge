package com.example.rqchallenge;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Profile("test")
@TestConfiguration
public class TestConfig {
    @Bean
    public RestTemplate restTemplate() {
        return Mockito.mock(RestTemplate.class);
    }
}
