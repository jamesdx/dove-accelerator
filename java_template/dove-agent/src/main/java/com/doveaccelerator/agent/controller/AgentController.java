package com.doveaccelerator.agent.controller;

import com.doveaccelerator.agent.dto.AgentDTO;
import com.doveaccelerator.agent.dto.AgentRoleDTO;
import com.doveaccelerator.agent.dto.AgentSkillDTO;
import com.doveaccelerator.agent.service.AgentService;
import com.doveaccelerator.common.model.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/agents")
@RequiredArgsConstructor
public class AgentController {

    private final AgentService agentService;

    @PostMapping
    public ApiResponse<AgentDTO> createAgent(@Valid @RequestBody AgentDTO agentDTO) {
        return ApiResponse.success(agentService.createAgent(agentDTO));
    }

    @PutMapping("/{id}")
    public ApiResponse<AgentDTO> updateAgent(@PathVariable Long id, @Valid @RequestBody AgentDTO agentDTO) {
        return ApiResponse.success(agentService.updateAgent(id, agentDTO));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAgent(@PathVariable Long id) {
        agentService.deleteAgent(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/{id}")
    public ApiResponse<AgentDTO> getAgent(@PathVariable Long id) {
        return ApiResponse.success(agentService.getAgent(id));
    }

    @GetMapping
    public ApiResponse<Page<AgentDTO>> getAllAgents(Pageable pageable) {
        return ApiResponse.success(agentService.getAllAgents(pageable));
    }

    @GetMapping("/role/{roleName}")
    public ApiResponse<List<AgentDTO>> getAgentsByRole(@PathVariable String roleName) {
        return ApiResponse.success(agentService.getAgentsByRole(roleName));
    }

    @GetMapping("/model/{modelType}")
    public ApiResponse<List<AgentDTO>> getAgentsByModelType(@PathVariable String modelType) {
        return ApiResponse.success(agentService.getAgentsByModelType(modelType));
    }

    @PostMapping("/{agentId}/roles")
    public ApiResponse<AgentRoleDTO> addRole(
            @PathVariable Long agentId,
            @Valid @RequestBody AgentRoleDTO roleDTO) {
        return ApiResponse.success(agentService.addRole(agentId, roleDTO));
    }

    @PostMapping("/{agentId}/skills")
    public ApiResponse<AgentSkillDTO> addSkill(
            @PathVariable Long agentId,
            @Valid @RequestBody AgentSkillDTO skillDTO) {
        return ApiResponse.success(agentService.addSkill(agentId, skillDTO));
    }

    @DeleteMapping("/{agentId}/roles/{roleId}")
    public ApiResponse<Void> removeRole(@PathVariable Long agentId, @PathVariable Long roleId) {
        agentService.removeRole(agentId, roleId);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{agentId}/skills/{skillId}")
    public ApiResponse<Void> removeSkill(@PathVariable Long agentId, @PathVariable Long skillId) {
        agentService.removeSkill(agentId, skillId);
        return ApiResponse.success(null);
    }

    @PutMapping("/{id}/activate")
    public ApiResponse<Void> activateAgent(@PathVariable Long id) {
        agentService.activateAgent(id);
        return ApiResponse.success(null);
    }

    @PutMapping("/{id}/deactivate")
    public ApiResponse<Void> deactivateAgent(@PathVariable Long id) {
        agentService.deactivateAgent(id);
        return ApiResponse.success(null);
    }
}