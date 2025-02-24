package com.helix.dove.dove.accelerator.entity;

public enum AgentRole {
    CUSTOMER("客户"),
    PROJECT_MANAGER("项目经理"),
    PRODUCT_MANAGER("产品经理"),
    ARCHITECT("架构师"),
    DEVELOPER("开发人员"),
    TESTER("测试人员"),
    OPERATIONS("运维人员");

    private final String displayName;

    AgentRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 