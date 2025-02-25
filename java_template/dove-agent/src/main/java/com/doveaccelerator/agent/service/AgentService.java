package com.doveaccelerator.agent.service;

import com.doveaccelerator.agent.dto.AgentDTO;
import com.doveaccelerator.agent.dto.AgentRoleDTO;
import com.doveaccelerator.agent.dto.AgentSkillDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AgentService {
    AgentDTO createAgent(AgentDTO agentDTO);
    
    AgentDTO updateAgent(Long id, AgentDTO agentDTO);
    
    void deleteAgent(Long id);
    
    AgentDTO getAgent(Long id);
    
    Page<AgentDTO> getAllAgents(Pageable pageable);
    
    List<AgentDTO> getAgentsByRole(String roleName);
    
    List<AgentDTO> getAgentsByModelType(String modelType);
    
    AgentRoleDTO addRole(Long agentId, AgentRoleDTO roleDTO);
    
    AgentSkillDTO addSkill(Long agentId, AgentSkillDTO skillDTO);
    
    void removeRole(Long agentId, Long roleId);
    
    void removeSkill(Long agentId, Long skillId);
    
    void activateAgent(Long id);
    
    void deactivateAgent(Long id);
}