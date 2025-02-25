package com.doveaccelerator.task.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {
    private Long id;

    @NotBlank(message = "validation.task.title.not.blank")
    @Size(min = 2, max = 200, message = "validation.task.title.size")
    private String title;

    @Size(max = 1000, message = "validation.task.description.size")
    private String description;

    @NotBlank(message = "validation.task.status.not.blank")
    private String status;

    @NotNull(message = "validation.task.priority.not.null")
    @Min(value = 1, message = "validation.task.priority.min")
    @Max(value = 5, message = "validation.task.priority.max")
    private Integer priority;

    @Min(value = 0, message = "validation.task.progress.min")
    @Max(value = 100, message = "validation.task.progress.max")
    private Integer progress = 0;

    @NotNull(message = "validation.task.project.id.not.null")
    private Long projectId;

    private Long phaseId;

    private Map<String, Object> taskConfig;

    private Map<String, Object> evaluationMetrics;

    private Set<TaskAssignmentDTO> assignments;

    private Set<TaskDependencyDTO> dependencies;

    private Double estimatedHours;

    private Double actualHours;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private LocalDateTime deadline;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}