package com.helix.dove.dove.accelerator.dto;

public record RequirementResponse(
    String title,
    String feedback,
    String type
) {
    public static RequirementResponse success(String title, String feedback) {
        return new RequirementResponse(title, feedback, "success");
    }

    public static RequirementResponse warning(String title, String feedback) {
        return new RequirementResponse(title, feedback, "warning");
    }

    public static RequirementResponse error(String title, String feedback) {
        return new RequirementResponse(title, feedback, "error");
    }
} 