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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.util.StringUtils;

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
                logger.debug("开始生成AI响应，prompt长度: {}", prompt.length());
                String response = callAnthropicAPI(prompt, parameters);
                logger.debug("收到AI原始响应: {}", response);
                
                // 尝试从响应中提取JSON内容
                String jsonContent = extractJsonContent(response);
                if (jsonContent != null) {
                    logger.debug("成功提取JSON内容: {}", jsonContent);
                    // 验证JSON格式是否正确
                    try {
                        objectMapper.readTree(jsonContent);
                        return jsonContent;
                    } catch (Exception e) {
                        logger.error("提取的JSON内容格式不正确: {}", e.getMessage());
                    }
                }
                
                logger.error("无法从AI响应中提取有效的JSON内容");
                throw new RuntimeException("AI响应中未找到有效的JSON内容");
            } catch (Exception e) {
                logger.error("生成AI响应失败: {}", e.getMessage(), e);
                throw new RuntimeException("生成AI响应失败: " + e.getMessage());
            }
        });
    }

    private String extractJsonContent(String response) {
        try {
            // 首先尝试解析整个响应为 JSON
            JsonNode rootNode = objectMapper.readTree(response);
            
            // 如果响应本身就是任务列表，直接返回
            if (rootNode.has("tasks")) {
                return response;
            }
            
            // 如果是 Anthropic API 响应格式，提取 content 中的 text
            if (rootNode.has("content") && rootNode.get("content").isArray()) {
                JsonNode content = rootNode.get("content").get(0);
                if (content.has("text")) {
                    String text = content.get("text").asText();
                    // 验证提取的文本是否包含任务列表
                    try {
                        JsonNode textNode = objectMapper.readTree(text);
                        if (textNode.has("tasks")) {
                            return text;
                        }
                    } catch (Exception e) {
                        logger.debug("提取的文本不是有效的JSON: {}", e.getMessage());
                    }
                }
            }
            
            // 如果上述方法都失败，尝试使用正则表达式查找包含 tasks 的 JSON
            Pattern pattern = Pattern.compile("\\{[^{}]*\"tasks\"[^{}]*(?:\\{[^{}]*}[^{}]*)*}");
            Matcher matcher = pattern.matcher(response);
            if (matcher.find()) {
                String jsonContent = matcher.group();
                try {
                    // 验证提取的内容是否为有效的任务列表 JSON
                    JsonNode node = objectMapper.readTree(jsonContent);
                    if (node.has("tasks")) {
                        return jsonContent;
                    }
                } catch (Exception e) {
                    logger.debug("正则表达式匹配的内容不是有效的JSON: {}", e.getMessage());
                }
            }
            
            logger.error("无法从响应中提取任务列表JSON");
            throw new RuntimeException("响应中未找到有效的任务列表JSON");
        } catch (Exception e) {
            logger.error("解析响应失败: {}", e.getMessage());
            throw new RuntimeException("解析响应失败: " + e.getMessage());
        }
    }

    private String callAnthropicAPI(String prompt, Map<String, Object> parameters) {
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
                        return text;
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