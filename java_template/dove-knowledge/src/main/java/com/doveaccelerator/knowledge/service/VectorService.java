package com.doveaccelerator.knowledge.service;

import java.util.Map;

public interface VectorService {
    Map<String, Object> generateVector(String text);
    
    String generateVectorString(String text);
    
    double calculateSimilarity(Map<String, Object> vector1, Map<String, Object> vector2);
    
    double calculateSimilarity(String text1, String text2);
}