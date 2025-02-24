package com.helix.dove.dove.accelerator.service.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class OpenAIProvider implements AIProvider {
    private static final Logger logger = LoggerFactory.getLogger(OpenAIProvider.class);
    private final ChatClient chatClient;

    @Autowired
    public OpenAIProvider(ChatClient chatClient) {
        this.chatClient = chatClient;
        logger.info("OpenAI提供商初始化完成");
    }

    @Override
    public String getProviderName() {
        return "openai";
    }

    @Override
    public CompletableFuture<String> generateResponse(String prompt, Map<String, Object> parameters) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("开始生成AI响应...");
                logger.debug("Prompt长度: {} 字符", prompt.length());
                logger.debug("使用参数: {}", parameters);

                // 应用参数
                Double temperature = parameters.containsKey("temperature") ? 
                    (Double) parameters.get("temperature") : 0.7;
                String model = parameters.containsKey("model") ? 
                    (String) parameters.get("model") : "gpt-3.5-turbo";

                logger.debug("使用模型: {}, 温度: {}", model, temperature);

                // TODO: 根据参数配置ChatClient
                logger.debug("发送请求到OpenAI API...");
                String result = chatClient.call(new Prompt(prompt))
                    .getResult().getOutput().getContent();

                logger.info("AI响应生成成功，响应长度: {} 字符", result.length());
                logger.debug("AI响应内容: {}", result);
                return result;
            } catch (Exception e) {
                logger.error("OpenAI API调用失败: {}", e.getMessage(), e);
                logger.error("错误详情: ", e);
                throw new RuntimeException("AI生成失败: " + e.getMessage());
            }
        });
    }

    @Override
    public boolean isAvailable() {
        try {
            logger.debug("正在检查OpenAI提供商可用性...");
            // 发送一个简单的测试请求
            chatClient.call(new Prompt("test"));
            logger.debug("OpenAI提供商可用性检查通过");
            return true;
        } catch (Exception e) {
            logger.warn("OpenAI提供商不可用: {}", e.getMessage());
            return false;
        }
    }
} 