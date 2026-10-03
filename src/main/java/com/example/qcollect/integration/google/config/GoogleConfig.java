package com.example.qcollect.integration.google.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class GoogleConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}