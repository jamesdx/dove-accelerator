package com.doveaccelerator.knowledge.controller;

import com.doveaccelerator.knowledge.dto.KnowledgeDTO;
import com.doveaccelerator.knowledge.service.KnowledgeService;
import com.doveaccelerator.common.model.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/knowledge")
@RequiredArgsConstructor
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    @PostMapping
    public ApiResponse<KnowledgeDTO> createKnowledge(@Valid @RequestBody KnowledgeDTO knowledgeDTO) {
        return ApiResponse.success(knowledgeService.createKnowledge(knowledgeDTO));
    }

    @PutMapping("/{id}")
    public ApiResponse<KnowledgeDTO> updateKnowledge(@PathVariable Long id, @Valid @RequestBody KnowledgeDTO knowledgeDTO) {
        return ApiResponse.success(knowledgeService.updateKnowledge(id, knowledgeDTO));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteKnowledge(@PathVariable Long id) {
        knowledgeService.deleteKnowledge(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/{id}")
    public ApiResponse<KnowledgeDTO> getKnowledge(@PathVariable Long id) {
        return ApiResponse.success(knowledgeService.getKnowledge(id));
    }

    @GetMapping
    public ApiResponse<Page<KnowledgeDTO>> getAllKnowledge(Pageable pageable) {
        return ApiResponse.success(knowledgeService.getAllKnowledge(pageable));
    }

    @GetMapping("/search")
    public ApiResponse<List<KnowledgeDTO>> searchByKeyword(@RequestParam String keyword) {
        return ApiResponse.success(knowledgeService.searchByKeyword(keyword));
    }

    @GetMapping("/search/vector")
    public ApiResponse<List<KnowledgeDTO>> searchByVector(
            @RequestParam String text,
            @RequestParam(defaultValue = "0.8") double threshold) {
        return ApiResponse.success(knowledgeService.searchByVector(text, threshold));
    }

    @GetMapping("/category/{categoryId}")
    public ApiResponse<List<KnowledgeDTO>> getByCategory(@PathVariable Long categoryId) {
        return ApiResponse.success(knowledgeService.getByCategory(categoryId));
    }

    @GetMapping("/tag/{tagName}")
    public ApiResponse<List<KnowledgeDTO>> getByTag(@PathVariable String tagName) {
        return ApiResponse.success(knowledgeService.getByTag(tagName));
    }

    @PostMapping("/{knowledgeId}/tags/{tagId}")
    public ApiResponse<Void> addTag(@PathVariable Long knowledgeId, @PathVariable Long tagId) {
        knowledgeService.addTag(knowledgeId, tagId);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{knowledgeId}/tags/{tagId}")
    public ApiResponse<Void> removeTag(@PathVariable Long knowledgeId, @PathVariable Long tagId) {
        knowledgeService.removeTag(knowledgeId, tagId);
        return ApiResponse.success(null);
    }

    @PutMapping("/{knowledgeId}/category/{categoryId}")
    public ApiResponse<Void> updateCategory(@PathVariable Long knowledgeId, @PathVariable Long categoryId) {
        knowledgeService.updateCategory(knowledgeId, categoryId);
        return ApiResponse.success(null);
    }

    @PostMapping("/{id}/vector")
    public ApiResponse<Void> updateVectorEmbedding(@PathVariable Long id) {
        knowledgeService.updateVectorEmbedding(id);
        return ApiResponse.success(null);
    }
}