package com.doveaccelerator.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.Map;
import java.util.HashMap;

@Data
@Configuration
@ConfigurationProperties(prefix = "prompt.engine")
public class PromptEngineConfig {

    // Default model configurations
    private Map<String, ModelConfig> models = new HashMap<>();
    
    // Template engine settings
    private TemplateConfig template = new TemplateConfig();
    
    // Evaluation metrics settings
    private EvaluationConfig evaluation = new EvaluationConfig();

    @Data
    public static class ModelConfig {
        private String apiEndpoint;
        private String apiKey;
        private Double defaultTemperature = 0.7;
        private Integer defaultMaxTokens = 2000;
        private Map<String, Object> parameters = new HashMap<>();
    }

    @Data
    public static class TemplateConfig {
        private boolean enableCache = true;
        private int cacheSize = 1000;
        private String defaultLanguage = "en";
        private Map<String, Object> engineParameters = new HashMap<>();
    }

    @Data
    public static class EvaluationConfig {
        private boolean enabled = true;
        private int sampleSize = 100;
        private double accuracyThreshold = 0.8;
        private double latencyThreshold = 2000;
        private Map<String, Double> metricWeights = new HashMap<>();
    }

    // Helper methods for runtime configuration
    public ModelConfig getModelConfig(String modelType) {
        return models.getOrDefault(modelType, new ModelConfig());
    }

    public void updateModelConfig(String modelType, ModelConfig config) {
        models.put(modelType, config);
    }

    public Map<String, Object> getEngineParameters() {
        return template.getEngineParameters();
    }

    public void setMetricWeight(String metric, Double weight) {
        evaluation.getMetricWeights().put(metric, weight);
    }
}