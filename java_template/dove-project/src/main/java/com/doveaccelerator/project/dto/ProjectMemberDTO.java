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
public class ProjectMemberDTO {
    private Long id;

    @NotNull(message = "validation.project.member.project.id.not.null")
    private Long projectId;

    @NotNull(message = "validation.project.member.agent.id.not.null")
    private Long agentId;

    @NotBlank(message = "validation.project.member.role.name.not.blank")
    private String roleName;

    private Map<String, Object> performanceMetrics;

    private Map<String, Object> assignedTasks;

    @NotNull(message = "validation.project.member.join.time.not.null")
    private LocalDateTime joinTime;

    private LocalDateTime leaveTime;

    private boolean active = true;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}