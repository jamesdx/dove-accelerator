package com.doveaccelerator.knowledge.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeTagDTO {
    private Long id;

    @NotBlank(message = "validation.tag.name.not.blank")
    @Size(min = 2, max = 50, message = "validation.tag.name.size")
    private String name;

    private String description;

    private Integer usageCount = 0;

    private boolean active = true;
}