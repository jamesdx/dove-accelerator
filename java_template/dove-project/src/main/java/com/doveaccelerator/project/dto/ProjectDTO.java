package com.doveaccelerator.project.dto;

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
public class ProjectDTO {
    private Long id;

    @NotBlank(message = "validation.project.name.not.blank")
    @Size(min = 2, max = 100, message = "validation.project.name.size")
    private String name;

    @Size(max = 1000, message = "validation.project.description.size")
    private String description;

    @NotBlank(message = "validation.project.status.not.blank")
    private String status;

    private String requirements;

    private Map<String, Object> technicalStack;

    private Map<String, Object> projectConfig;

    private Set<ProjectMemberDTO> members;

    private Set<ProjectPhaseDTO> phases;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}