package com.doveaccelerator.agent.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;
import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentDTO {
    private Long id;

    @NotBlank(message = "validation.agent.name.not.blank")
    @Size(min = 2, max = 100, message = "validation.agent.name.size")
    private String name;

    @NotBlank(message = "validation.agent.model.type.not.blank")
    private String modelType;

    @NotBlank(message = "validation.agent.status.not.blank")
    private String status;

    private Map<String, Object> config;

    private Set<AgentRoleDTO> roles;

    private Set<AgentSkillDTO> skills;
}