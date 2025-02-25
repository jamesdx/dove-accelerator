package com.doveaccelerator.agent.service.impl;

import com.doveaccelerator.agent.dto.AgentDTO;
import com.doveaccelerator.agent.dto.AgentRoleDTO;
import com.doveaccelerator.agent.dto.AgentSkillDTO;
import com.doveaccelerator.agent.entity.Agent;
import com.doveaccelerator.agent.entity.AgentRole;
import com.doveaccelerator.agent.entity.AgentSkill;
import com.doveaccelerator.agent.mapper.AgentMapper;
import com.doveaccelerator.agent.repository.AgentRepository;
import com.doveaccelerator.agent.service.AgentService;
import com.doveaccelerator.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgentServiceImpl implements AgentService {

    private final AgentRepository agentRepository;
    private final AgentMapper agentMapper;

    @Override
    @Transactional
    public AgentDTO createAgent(AgentDTO agentDTO) {
        Agent agent = agentMapper.toEntity(agentDTO);
        agent = agentRepository.save(agent);
        return agentMapper.toDTO(agent);
    }

    @Override
    @Transactional
    public AgentDTO updateAgent(Long id, AgentDTO agentDTO) {
        Agent agent = agentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("agent.not.found"));
        
        agentMapper.updateEntity(agentDTO, agent);
        agent = agentRepository.save(agent);
        return agentMapper.toDTO(agent);
    }

    @Override
    @Transactional
    public void deleteAgent(Long id) {
        agentRepository.deleteById(id);
    }

    @Override
    public AgentDTO getAgent(Long id) {
        Agent agent = agentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("agent.not.found"));
        return agentMapper.toDTO(agent);
    }

    @Override
    public Page<AgentDTO> getAllAgents(Pageable pageable) {
        return agentRepository.findAll(pageable)
                .map(agentMapper::toDTO);
    }

    @Override
    public List<AgentDTO> getAgentsByRole(String roleName) {
        return agentRepository.findByRoleName(roleName).stream()
                .map(agentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AgentDTO> getAgentsByModelType(String modelType) {
        return agentRepository.findByModelType(modelType).stream()
                .map(agentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AgentRoleDTO addRole(Long agentId, AgentRoleDTO roleDTO) {
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new BusinessException("agent.not.found"));
        
        AgentRole role = agentMapper.toRoleEntity(roleDTO);
        role.setAgent(agent);
        agent.getRoles().add(role);
        
        agent = agentRepository.save(agent);
        return agentMapper.toRoleDTO(role);
    }

    @Override
    @Transactional
    public AgentSkillDTO addSkill(Long agentId, AgentSkillDTO skillDTO) {
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new BusinessException("agent.not.found"));
        
        AgentSkill skill = agentMapper.toSkillEntity(skillDTO);
        skill.setAgent(agent);
        agent.getSkills().add(skill);
        
        agent = agentRepository.save(agent);
        return agentMapper.toSkillDTO(skill);
    }

    @Override
    @Transactional
    public void removeRole(Long agentId, Long roleId) {
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new BusinessException("agent.not.found"));
        
        agent.getRoles().removeIf(role -> role.getId().equals(roleId));
        agentRepository.save(agent);
    }

    @Override
    @Transactional
    public void removeSkill(Long agentId, Long skillId) {
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new BusinessException("agent.not.found"));
        
        agent.getSkills().removeIf(skill -> skill.getId().equals(skillId));
        agentRepository.save(agent);
    }

    @Override
    @Transactional
    public void activateAgent(Long id) {
        Agent agent = agentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("agent.not.found"));
        agent.setStatus("ACTIVE");
        agentRepository.save(agent);
    }

    @Override
    @Transactional
    public void deactivateAgent(Long id) {
        Agent agent = agentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("agent.not.found"));
        agent.setStatus("INACTIVE");
        agentRepository.save(agent);
    }
}