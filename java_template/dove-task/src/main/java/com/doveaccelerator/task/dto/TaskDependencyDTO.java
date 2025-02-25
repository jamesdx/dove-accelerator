package com.doveaccelerator.task.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDependencyDTO {
    private Long id;

    @NotNull(message = "validation.task.dependency.source.task.id.not.null")
    private Long sourceTaskId;

    @NotNull(message = "validation.task.dependency.target.task.id.not.null")
    private Long targetTaskId;

    @NotBlank(message = "validation.task.dependency.type.not.blank")
    private String dependencyType;

    @NotBlank(message = "validation.task.dependency.status.not.blank")
    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}