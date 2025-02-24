package com.helix.dove.dove.accelerator.service.ai;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class AIProviderFactory {
    private final Map<String, AIProvider> providers;
    private final String defaultProvider;

    public AIProviderFactory(List<AIProvider> providers) {
        this.providers = providers.stream()
                .collect(Collectors.toMap(
                    AIProvider::getProviderName,
                    Function.identity()
                ));
        
        // 设置默认提供商为 anthropic
        this.defaultProvider = "anthropic";
    }

    public AIProvider getProvider(String providerName) {
        return Optional.ofNullable(providerName)
                .map(name -> providers.get(name.toLowerCase()))
                .filter(AIProvider::isAvailable)
                .orElseGet(this::getDefaultProvider);
    }

    public AIProvider getDefaultProvider() {
        return Optional.ofNullable(providers.get(defaultProvider))
                .filter(AIProvider::isAvailable)
                .orElseThrow(() -> new RuntimeException("No available AI provider found"));
    }

    public List<String> getAvailableProviders() {
        return providers.values().stream()
                .filter(AIProvider::isAvailable)
                .map(AIProvider::getProviderName)
                .collect(Collectors.toList());
    }
} 