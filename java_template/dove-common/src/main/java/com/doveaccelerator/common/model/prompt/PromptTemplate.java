package com.doveaccelerator.common.model.prompt;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromptTemplate {
    private Long id;
    private String name;
    private String description;
    private String type;  // ROLE_PLAYING, STEP_BY_STEP, EXAMPLE_COMPARISON, CHAIN_OF_THOUGHT, REACT
    private String systemPrompt;
    private String userPrompt;
    private String assistantPrompt;
    private Map<String, String> examples;  // For few-shot learning
    private String version;
    private boolean active;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
    private Map<String, Object> metadata;  // Additional configuration
}