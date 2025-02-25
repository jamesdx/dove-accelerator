package com.doveaccelerator.knowledge.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeCategoryDTO {
    private Long id;

    @NotBlank(message = "validation.category.name.not.blank")
    @Size(min = 2, max = 50, message = "validation.category.name.size")
    private String name;

    private String description;

    private Long parentId;

    private boolean active = true;

    private Integer displayOrder;
}