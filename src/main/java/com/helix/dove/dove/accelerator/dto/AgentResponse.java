package com.helix.dove.dove.accelerator.dto;

public record AgentResponse(
    Long id,
    String name,
    String role,
    String status,
    String aiProvider,
    String currentTask
) {} 