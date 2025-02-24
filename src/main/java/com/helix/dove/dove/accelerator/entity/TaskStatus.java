package com.helix.dove.dove.accelerator.entity;

public enum TaskStatus {
    PENDING("待处理"),
    IN_PROGRESS("进行中"),
    BLOCKED("被阻塞"),
    COMPLETED("已完成"),
    CANCELLED("已取消");

    private final String displayName;

    TaskStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 