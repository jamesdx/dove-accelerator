package com.doveaccelerator.knowledge.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeDTO {
    private Long id;

    @NotBlank(message = "validation.knowledge.title.not.blank")
    @Size(min = 2, max = 200, message = "validation.knowledge.title.size")
    private String title;

    @NotBlank(message = "validation.knowledge.content.not.blank")
    private String content;

    private Long categoryId;

    private Set<Long> tagIds;

    private Map<String, Object> vectorEmbedding;

    @NotBlank(message = "validation.knowledge.status.not.blank")
    private String status;

    private String sourceType;

    private String sourceUrl;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}