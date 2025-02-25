package com.doveaccelerator.knowledge.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;
import java.util.Map;
import java.util.HashMap;

@Data
@Configuration
@ConfigurationProperties(prefix = "embedding")
public class EmbeddingConfig {
    private String provider = "openai";
    private String model = "text-embedding-3-small";
    private int dimension = 384;
    private double similarityThreshold = 0.8;
    private Map<String, Object> parameters = new HashMap<>();
    
    private final Map<String, ProviderConfig> providers = new HashMap<>();
    
    @Data
    public static class ProviderConfig {
        private String apiEndpoint;
        private String apiKey;
        private String model;
        private Map<String, Object> parameters = new HashMap<>();
    }
    
    public ProviderConfig getActiveProvider() {
        return providers.get(provider);
    }
}