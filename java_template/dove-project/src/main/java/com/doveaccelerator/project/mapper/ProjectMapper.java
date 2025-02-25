package com.doveaccelerator.project.mapper;

import com.doveaccelerator.project.dto.ProjectDTO;
import com.doveaccelerator.project.dto.ProjectMemberDTO;
import com.doveaccelerator.project.dto.ProjectPhaseDTO;
import com.doveaccelerator.project.entity.Project;
import com.doveaccelerator.project.entity.ProjectMember;
import com.doveaccelerator.project.entity.ProjectPhase;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {
    
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "phases", ignore = true)
    Project toEntity(ProjectDTO dto);

    @Mapping(target = "members", ignore = true)
    @Mapping(target = "phases", ignore = true)
    ProjectDTO toDTO(Project entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "phases", ignore = true)
    void updateEntity(ProjectDTO dto, @MappingTarget Project entity);

    @Mapping(target = "project", ignore = true)
    ProjectMember toMemberEntity(ProjectMemberDTO dto);

    @Mapping(target = "projectId", source = "project.id")
    ProjectMemberDTO toMemberDTO(ProjectMember entity);

    @Mapping(target = "project", ignore = true)
    ProjectPhase toPhaseEntity(ProjectPhaseDTO dto);

    @Mapping(target = "projectId", source = "project.id")
    ProjectPhaseDTO toPhaseDTO(ProjectPhase entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", ignore = true)
    void updatePhaseEntity(ProjectPhaseDTO dto, @MappingTarget ProjectPhase entity);
}