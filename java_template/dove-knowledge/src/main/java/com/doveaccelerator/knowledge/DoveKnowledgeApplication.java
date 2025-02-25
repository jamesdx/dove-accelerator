package com.doveaccelerator.knowledge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableDiscoveryClient
@EnableAsync
@EnableTransactionManagement
public class DoveKnowledgeApplication {
    public static void main(String[] args) {
        SpringApplication.run(DoveKnowledgeApplication.class, args);
    }
}