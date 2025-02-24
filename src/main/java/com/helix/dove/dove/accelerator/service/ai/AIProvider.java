package com.helix.dove.dove.accelerator.service.ai;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * AI Provider interface for different AI vendors
 */
public interface AIProvider {
    /**
     * Get the provider name
     * @return provider name (e.g., "openai", "anthropic", "google")
     */
    String getProviderName();

    /**
     * Send prompt to AI and get response
     * @param prompt The prompt text
     * @param parameters Additional parameters for the AI call
     * @return CompletableFuture with the AI response
     */
    CompletableFuture<String> generateResponse(String prompt, Map<String, Object> parameters);

    /**
     * Check if the provider is available
     * @return true if the provider is properly configured and available
     */
    boolean isAvailable();
} 