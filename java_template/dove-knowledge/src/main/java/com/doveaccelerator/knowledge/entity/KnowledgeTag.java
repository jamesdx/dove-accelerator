package com.doveaccelerator.knowledge.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Entity
@Table(name = "knowledge_tags")
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @Column(name = "usage_count")
    private Integer usageCount = 0;

    @Column(nullable = false)
    private boolean active = true;
}