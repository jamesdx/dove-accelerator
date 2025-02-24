package com.helix.dove.dove.accelerator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RequirementRequest(
    @NotBlank(message = "需求描述不能为空")
    @Size(min = 10, max = 500, message = "需求描述长度必须在10-500字符之间")
    String userBasicRequriement
) {} 