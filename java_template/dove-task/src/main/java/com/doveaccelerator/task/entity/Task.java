package com.doveaccelerator.task.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@Entity
@Table(name = "task")
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String status;

    @Column(name = "priority", nullable = false)
    private Integer priority;

    @Column(name = "progress", nullable = false)
    private Integer progress = 0;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "phase_id")
    private Long phaseId;

    @Column(name = "task_config", columnDefinition = "json")
    private Map<String, Object> taskConfig;

    @Column(name = "evaluation_metrics", columnDefinition = "json")
    private Map<String, Object> evaluationMetrics;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<TaskAssignment> assignments = new HashSet<>();

    @OneToMany(mappedBy = "sourceTask", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<TaskDependency> dependencies = new HashSet<>();

    @Column(name = "estimated_hours")
    private Double estimatedHours;

    @Column(name = "actual_hours")
    private Double actualHours;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "deadline")
    private LocalDateTime deadline;

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