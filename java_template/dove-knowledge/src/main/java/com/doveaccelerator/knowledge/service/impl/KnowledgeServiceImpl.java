package com.doveaccelerator.knowledge.service.impl;

import com.doveaccelerator.knowledge.dto.KnowledgeDTO;
import com.doveaccelerator.knowledge.entity.Knowledge;
import com.doveaccelerator.knowledge.mapper.KnowledgeMapper;
import com.doveaccelerator.knowledge.repository.KnowledgeRepository;
import com.doveaccelerator.knowledge.service.KnowledgeService;
import com.doveaccelerator.knowledge.service.VectorService;
import com.doveaccelerator.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KnowledgeServiceImpl implements KnowledgeService {

    private final KnowledgeRepository knowledgeRepository;
    private final KnowledgeMapper knowledgeMapper;
    private final VectorService vectorService;

    @Override
    @Transactional
    public KnowledgeDTO createKnowledge(KnowledgeDTO knowledgeDTO) {
        Knowledge knowledge = knowledgeMapper.toEntity(knowledgeDTO);
        // Generate vector embedding for content
        knowledge.setVectorEmbedding(vectorService.generateVector(knowledgeDTO.getContent()));
        knowledge = knowledgeRepository.save(knowledge);
        return knowledgeMapper.toDTO(knowledge);
    }

    @Override
    @Transactional
    public KnowledgeDTO updateKnowledge(Long id, KnowledgeDTO knowledgeDTO) {
        Knowledge knowledge = knowledgeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("knowledge.not.found"));
        
        knowledgeMapper.updateEntity(knowledgeDTO, knowledge);
        // Update vector embedding if content changed
        if (knowledgeDTO.getContent() != null) {
            knowledge.setVectorEmbedding(vectorService.generateVector(knowledgeDTO.getContent()));
        }
        knowledge = knowledgeRepository.save(knowledge);
        return knowledgeMapper.toDTO(knowledge);
    }

    @Override
    @Transactional
    public void deleteKnowledge(Long id) {
        knowledgeRepository.deleteById(id);
    }

    @Override
    public KnowledgeDTO getKnowledge(Long id) {
        Knowledge knowledge = knowledgeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("knowledge.not.found"));
        return knowledgeMapper.toDTO(knowledge);
    }

    @Override
    public Page<KnowledgeDTO> getAllKnowledge(Pageable pageable) {
        return knowledgeRepository.findAll(pageable)
                .map(knowledgeMapper::toDTO);
    }

    @Override
    public List<KnowledgeDTO> searchByKeyword(String keyword) {
        return knowledgeRepository.searchByKeyword(keyword).stream()
                .map(knowledgeMapper::toDTO)
                .toList();
    }

    @Override
    public List<KnowledgeDTO> searchByVector(String text, double threshold) {
        String queryVector = vectorService.generateVectorString(text);
        return knowledgeRepository.findSimilarByVector(queryVector, threshold).stream()
                .map(knowledgeMapper::toDTO)
                .toList();
    }

    @Override
    public List<KnowledgeDTO> getByCategory(Long categoryId) {
        return knowledgeRepository.findByCategoryId(categoryId).stream()
                .map(knowledgeMapper::toDTO)
                .toList();
    }

    @Override
    public List<KnowledgeDTO> getByTag(String tagName) {
        return knowledgeRepository.findByTagsName(tagName).stream()
                .map(knowledgeMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public void addTag(Long knowledgeId, Long tagId) {
        // Implementation for adding tag
    }

    @Override
    @Transactional
    public void removeTag(Long knowledgeId, Long tagId) {
        // Implementation for removing tag
    }

    @Override
    @Transactional
    public void updateCategory(Long knowledgeId, Long categoryId) {
        // Implementation for updating category
    }

    @Override
    @Transactional
    public void updateVectorEmbedding(Long id) {
        Knowledge knowledge = knowledgeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("knowledge.not.found"));
        knowledge.setVectorEmbedding(vectorService.generateVector(knowledge.getContent()));
        knowledgeRepository.save(knowledge);
    }
}