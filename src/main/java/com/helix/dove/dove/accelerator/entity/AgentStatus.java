package com.helix.dove.dove.accelerator.entity;

public enum AgentStatus {
    IDLE("空闲"),
    WORKING("工作中"),
    WAITING_FOR_RESPONSE("等待响应"),
    BLOCKED("被阻塞");

    private final String displayName;

    AgentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 