package com.doveaccelerator.knowledge.repository;

import com.doveaccelerator.knowledge.entity.Knowledge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface KnowledgeRepository extends JpaRepository<Knowledge, Long> {
    List<Knowledge> findByCategoryId(Long categoryId);
    
    List<Knowledge> findByTagsName(String tagName);
    
    @Query("SELECT k FROM Knowledge k WHERE k.title LIKE %:keyword% OR k.content LIKE %:keyword%")
    List<Knowledge> searchByKeyword(String keyword);
    
    @Query(value = "SELECT * FROM knowledge WHERE vector_embedding <-> :queryVector < :threshold", nativeQuery = true)
    List<Knowledge> findSimilarByVector(String queryVector, double threshold);
    
    List<Knowledge> findBySourceType(String sourceType);
    
    List<Knowledge> findByStatus(String status);
}