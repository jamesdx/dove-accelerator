package com.doveaccelerator.agent.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.util.Map;

@Data
@Entity
@Table(name = "agent_role")
@NoArgsConstructor
@AllArgsConstructor
public class AgentRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false)
    private Agent agent;

    @Column(name = "role_name", nullable = false)
    private String roleName;

    @Column(name = "experience_years", nullable = false)
    private Integer experienceYears;

    @Column(name = "capabilities", columnDefinition = "json")
    private Map<String, Object> capabilities;

    @Column(name = "active", nullable = false)
    private boolean active = true;
}