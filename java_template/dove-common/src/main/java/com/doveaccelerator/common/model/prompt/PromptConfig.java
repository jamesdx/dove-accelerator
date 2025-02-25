package com.doveaccelerator.common.model.prompt;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromptConfig {
    private Long id;
    private Long templateId;
    private String modelType;  // GPT-4, CLAUDE, WENXIN, etc.
    private Map<String, Object> parameters;  // Model specific parameters
    private Map<String, String> variableBindings;  // Template variable bindings
    private Double temperature;
    private Integer maxTokens;
    private String stopSequence;
    private Map<String, Object> additionalConfig;  // Additional model-specific config
    private boolean active;
}