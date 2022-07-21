package com.example.rqchallenge;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class Config {
    @Value("${external.base.url}")
    private String externalEmployeeServiceAPiUrl;
    @Value("${external.api.version}")
    private String externalEmployeeServiceAPiVersion;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        DefaultUriBuilderFactory defaultUrlFactory = new DefaultUriBuilderFactory(
                externalEmployeeServiceAPiUrl + "/" + externalEmployeeServiceAPiVersion
        );
        restTemplate.setUriTemplateHandler(defaultUrlFactory);
        return restTemplate;
    }
}
