package com.helix.dove.dove.accelerator.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "agents")
public class Agent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AgentRole role;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AgentStatus status = AgentStatus.IDLE;

    @Column(name = "ai_provider")
    private String aiProvider = "anthropic"; // 默认使用 Anthropic

    @Column(columnDefinition = "TEXT")
    private String currentTask;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 