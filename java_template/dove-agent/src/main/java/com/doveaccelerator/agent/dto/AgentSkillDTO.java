package com.doveaccelerator.agent.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentSkillDTO {
    private Long id;

    @NotNull(message = "validation.agent.skill.agent.id.not.null")
    private Long agentId;

    @NotBlank(message = "validation.agent.skill.name.not.blank")
    @Size(min = 2, max = 50, message = "validation.agent.skill.name.size")
    private String skillName;

    @NotNull(message = "validation.agent.skill.proficiency.level.not.null")
    @Min(value = 1, message = "validation.agent.skill.proficiency.level.min")
    @Max(value = 10, message = "validation.agent.skill.proficiency.level.max")
    private Integer proficiencyLevel;

    @Size(max = 500, message = "validation.agent.skill.description.size")
    private String description;

    private boolean active = true;
}