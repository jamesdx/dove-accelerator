package com.helix.dove.dove.accelerator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class AITaskResponse {
    private List<TaskItem> tasks;

    @Data
    public static class TaskItem {
        private String title;
        private String description;
        private String role;
        private String priority;
        private List<Long> dependencies;
        
        @JsonProperty("estimatedDuration")
        private Integer estimatedDuration;
    }
} 