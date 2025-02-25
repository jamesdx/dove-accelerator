package com.doveaccelerator.agent.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentRoleDTO {
    private Long id;

    @NotNull(message = "validation.agent.role.agent.id.not.null")
    private Long agentId;

    @NotBlank(message = "validation.agent.role.name.not.blank")
    @Size(min = 2, max = 50, message = "validation.agent.role.name.size")
    private String roleName;

    @NotNull(message = "validation.agent.role.experience.years.not.null")
    @Min(value = 20, message = "validation.agent.role.experience.years.min")
    private Integer experienceYears;

    private Map<String, Object> capabilities;

    private boolean active = true;
}