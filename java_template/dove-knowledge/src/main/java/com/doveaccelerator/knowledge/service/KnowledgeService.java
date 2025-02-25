package com.doveaccelerator.knowledge.service;

import com.doveaccelerator.knowledge.dto.KnowledgeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface KnowledgeService {
    KnowledgeDTO createKnowledge(KnowledgeDTO knowledgeDTO);
    
    KnowledgeDTO updateKnowledge(Long id, KnowledgeDTO knowledgeDTO);
    
    void deleteKnowledge(Long id);
    
    KnowledgeDTO getKnowledge(Long id);
    
    Page<KnowledgeDTO> getAllKnowledge(Pageable pageable);
    
    List<KnowledgeDTO> searchByKeyword(String keyword);
    
    List<KnowledgeDTO> searchByVector(String text, double threshold);
    
    List<KnowledgeDTO> getByCategory(Long categoryId);
    
    List<KnowledgeDTO> getByTag(String tagName);
    
    void addTag(Long knowledgeId, Long tagId);
    
    void removeTag(Long knowledgeId, Long tagId);
    
    void updateCategory(Long knowledgeId, Long categoryId);
    
    void updateVectorEmbedding(Long id);
}