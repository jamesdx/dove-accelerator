package com.doveaccelerator.agent.mapper;

import com.doveaccelerator.agent.dto.AgentDTO;
import com.doveaccelerator.agent.dto.AgentRoleDTO;
import com.doveaccelerator.agent.dto.AgentSkillDTO;
import com.doveaccelerator.agent.entity.Agent;
import com.doveaccelerator.agent.entity.AgentRole;
import com.doveaccelerator.agent.entity.AgentSkill;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AgentMapper {
    
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "skills", ignore = true)
    Agent toEntity(AgentDTO dto);

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "skills", ignore = true)
    AgentDTO toDTO(Agent entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "skills", ignore = true)
    void updateEntity(AgentDTO dto, @MappingTarget Agent entity);

    @Mapping(target = "agent", ignore = true)
    AgentRole toRoleEntity(AgentRoleDTO dto);

    @Mapping(target = "agentId", source = "agent.id")
    AgentRoleDTO toRoleDTO(AgentRole entity);

    @Mapping(target = "agent", ignore = true)
    AgentSkill toSkillEntity(AgentSkillDTO dto);

    @Mapping(target = "agentId", source = "agent.id")
    AgentSkillDTO toSkillDTO(AgentSkill entity);
}