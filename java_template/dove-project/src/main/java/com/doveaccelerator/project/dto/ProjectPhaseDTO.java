package com.doveaccelerator.project.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectPhaseDTO {
    private Long id;

    @NotNull(message = "validation.project.phase.project.id.not.null")
    private Long projectId;

    @NotBlank(message = "validation.project.phase.name.not.blank")
    @Size(min = 2, max = 100, message = "validation.project.phase.name.size")
    private String name;

    @Size(max = 1000, message = "validation.project.phase.description.size")
    private String description;

    @NotBlank(message = "validation.project.phase.status.not.blank")
    private String status;

    private Map<String, Object> objectives;

    private Map<String, Object> completionCriteria;

    @Min(value = 0, message = "validation.project.phase.progress.min")
    @Max(value = 100, message = "validation.project.phase.progress.max")
    private Integer progress = 0;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}