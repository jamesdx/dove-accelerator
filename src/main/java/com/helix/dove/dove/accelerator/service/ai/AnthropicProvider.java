package com.helix.dove.dove.accelerator.service.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.HashMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;

@Service
public class AnthropicProvider implements AIProvider {
    private static final Logger logger = LoggerFactory.getLogger(AnthropicProvider.class);
    private final RestClient restClient;
    private final String apiKey;
    private final String apiUrl;
    private final ObjectMapper objectMapper;

    public AnthropicProvider(
            @Value("${ai.anthropic.api-key:}") String apiKey,
            @Value("${ai.anthropic.api-url:https://api.anthropic.com/v1}") String apiUrl,
            @Autowired ObjectMapper objectMapper) {
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
        this.objectMapper = objectMapper;
        logger.info("正在初始化Anthropic AI提供商...");
        logger.debug("Anthropic API URL: {}", apiUrl);
        
        if (apiKey == null || apiKey.isEmpty()) {
            logger.warn("Anthropic API密钥未设置");
            this.restClient = null;
            return;
        }

        try {
            this.restClient = RestClient.builder()
                    .baseUrl(apiUrl)
                    .defaultHeader("x-api-key", apiKey)
                    .defaultHeader("anthropic-version", "2023-06-01")
                    .build();
            logger.info("Anthropic AI提供商初始化完成");
        } catch (Exception e) {
            logger.error("Anthropic AI提供商初始化失败: {}", e.getMessage(), e);
            throw new RuntimeException("Anthropic AI提供商初始化失败", e);
        }
    }

    @Override
    public String getProviderName() {
        return "anthropic";
    }

    @Override
    public CompletableFuture<String> generateResponse(String prompt, Map<String, Object> parameters) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (restClient == null) {
                    throw new RuntimeException("Anthropic AI提供商未正确初始化");
                }

                logger.info("开始生成AI响应...");
                logger.debug("Prompt长度: {} 字符", prompt.length());
                logger.debug("使用参数: {}", parameters);

                // 构建请求体
                Map<String, Object> requestBody = Map.of(
                    "model", parameters.getOrDefault("model", "claude-3-opus-20240229"),
                    "max_tokens", parameters.getOrDefault("max_tokens", 4096),
                    "temperature", parameters.getOrDefault("temperature", 0.7),
                    "messages", List.of(
                        Map.of(
                            "role", "user",
                            "content", prompt
                        )
                    )
                );

                logger.debug("发送请求到Anthropic API...");
                // 发送请求
                @SuppressWarnings("unchecked")
                Map<String, Object> response = restClient.post()
                    .uri("/messages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

                if (response != null) {
                    logger.debug("收到API响应: {}", response);
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> content = (List<Map<String, Object>>) response.get("content");
                    if (content != null && !content.isEmpty()) {
                        Map<String, Object> firstContent = content.get(0);
                        String text = (String) firstContent.get("text");
                        if (text != null) {
                            logger.info("AI响应生成成功，响应长度: {} 字符", text.length());
                            logger.debug("AI响应内容: {}", text);
                            
                            // 修改JSON提取和验证逻辑
                            try {
                                // 尝试将整个响应解析为JSON
                                JsonNode jsonNode = objectMapper.readTree(text);
                                if (jsonNode.has("tasks")) {
                                    logger.debug("成功解析JSON响应，包含tasks字段");
                                    return text;
                                }
                            } catch (Exception e) {
                                // 如果整个响应不是JSON，尝试提取JSON部分
                                int jsonStart = text.indexOf('{');
                                int jsonEnd = text.lastIndexOf('}') + 1;
                                if (jsonStart >= 0 && jsonEnd > jsonStart) {
                                    String jsonText = text.substring(jsonStart, jsonEnd);
                                    try {
                                        // 验证提取的内容是否为有效JSON且包含tasks字段
                                        JsonNode extractedNode = objectMapper.readTree(jsonText);
                                        if (extractedNode.has("tasks")) {
                                            logger.debug("成功提取JSON内容: {}", jsonText);
                                            return jsonText;
                                        }
                                    } catch (Exception ex) {
                                        logger.warn("提取的内容不是有效的JSON: {}", ex.getMessage());
                                    }
                                }
                                
                                // 如果无法提取有效的JSON，返回默认的任务列表
                                logger.warn("无法从响应中提取有效的JSON，构造默认任务列表");
                                Map<String, Object> defaultTasks = new HashMap<>();
                                List<Map<String, Object>> tasksList = new ArrayList<>();
                                
                                // 添加默认任务
                                Map<String, Object> task = new HashMap<>();
                                task.put("title", "需求分析");
                                task.put("description", "分析用户需求并创建详细的功能规格说明");
                                task.put("role", "PRODUCT_MANAGER");
                                task.put("priority", "HIGH");
                                task.put("estimatedDuration", 8);
                                tasksList.add(task);
                                
                                defaultTasks.put("tasks", tasksList);
                                return objectMapper.writeValueAsString(defaultTasks);
                            }
                        }
                    }
                }

                String errorMsg = "Invalid response format from Anthropic API";
                logger.error(errorMsg);
                logger.error("Actual response: {}", response);
                throw new RuntimeException(errorMsg);
            } catch (Exception e) {
                logger.error("Anthropic API调用失败: {}", e.getMessage(), e);
                logger.error("错误详情: ", e);
                throw new RuntimeException("AI生成失败: " + e.getMessage());
            }
        });
    }

    @Override
    public boolean isAvailable() {
        boolean isAvailable = apiKey != null && !apiKey.isEmpty() && restClient != null;
        logger.debug("Anthropic AI提供商可用性检查: {}", isAvailable);
        if (!isAvailable) {
            logger.warn("Anthropic AI提供商不可用: API密钥未设置或初始化失败");
        }
        return isAvailable;
    }
} 