package com.doveaccelerator.task.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Entity
@Table(name = "task_assignment")
@NoArgsConstructor
@AllArgsConstructor
public class TaskAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @Column(name = "agent_id", nullable = false)
    private Long agentId;

    @Column(name = "role_name", nullable = false)
    private String roleName;

    @Column(nullable = false)
    private String status;

    @Column(name = "performance_metrics", columnDefinition = "json")
    private Map<String, Object> performanceMetrics;

    @Column(name = "assignment_config", columnDefinition = "json")
    private Map<String, Object> assignmentConfig;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "hours_spent")
    private Double hoursSpent;

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