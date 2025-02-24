package com.helix.dove.dove.accelerator.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RequirementRequest {
    @NotBlank(message = "需求描述不能为空")
    private String userBasicRequirement;
} 