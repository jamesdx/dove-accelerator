package com.doveaccelerator.agent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConfigurationProperties(prefix = "agent")
public class AgentConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}