package com.doveaccelerator.task.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskAssignmentDTO {
    private Long id;

    @NotNull(message = "validation.task.assignment.task.id.not.null")
    private Long taskId;

    @NotNull(message = "validation.task.assignment.agent.id.not.null")
    private Long agentId;

    @NotBlank(message = "validation.task.assignment.role.name.not.blank")
    private String roleName;

    @NotBlank(message = "validation.task.assignment.status.not.blank")
    private String status;

    private Map<String, Object> performanceMetrics;

    private Map<String, Object> assignmentConfig;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Double hoursSpent;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}