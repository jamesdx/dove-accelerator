package com.helix.dove.dove.accelerator.controller;

import com.helix.dove.dove.accelerator.dto.AgentResponse;
import com.helix.dove.dove.accelerator.entity.Agent;
import com.helix.dove.dove.accelerator.repository.AgentRepository;
import com.helix.dove.dove.accelerator.service.ai.AIProviderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/agents")
public class AgentController {

    private final AgentRepository agentRepository;
    private final AIProviderFactory aiProviderFactory;

    @Autowired
    public AgentController(AgentRepository agentRepository, AIProviderFactory aiProviderFactory) {
        this.agentRepository = agentRepository;
        this.aiProviderFactory = aiProviderFactory;
    }

    @GetMapping
    public ResponseEntity<List<AgentResponse>> getAllAgents() {
        List<AgentResponse> agents = agentRepository.findAll().stream()
            .map(agent -> new AgentResponse(
                agent.getId(),
                agent.getName(),
                agent.getRole().name(),
                agent.getStatus().name(),
                agent.getAiProvider(),
                agent.getCurrentTask()
            ))
            .collect(Collectors.toList());
        return ResponseEntity.ok(agents);
    }

    @PutMapping("/{id}/ai-provider")
    public ResponseEntity<?> updateAIProvider(
            @PathVariable Long id,
            @RequestParam String provider) {
        // 检查提供商是否可用
        if (!aiProviderFactory.getAvailableProviders().contains(provider)) {
            return ResponseEntity.badRequest()
                .body(Map.of(
                    "error", "Invalid AI provider",
                    "availableProviders", aiProviderFactory.getAvailableProviders()
                ));
        }

        return agentRepository.findById(id)
            .map(agent -> {
                agent.setAiProvider(provider);
                Agent savedAgent = agentRepository.save(agent);
                return ResponseEntity.ok(new AgentResponse(
                    savedAgent.getId(),
                    savedAgent.getName(),
                    savedAgent.getRole().name(),
                    savedAgent.getStatus().name(),
                    savedAgent.getAiProvider(),
                    savedAgent.getCurrentTask()
                ));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/available-providers")
    public ResponseEntity<List<String>> getAvailableProviders() {
        return ResponseEntity.ok(aiProviderFactory.getAvailableProviders());
    }
} 