package com.doveaccelerator.project.service.impl;

import com.doveaccelerator.project.dto.ProjectDTO;
import com.doveaccelerator.project.dto.ProjectMemberDTO;
import com.doveaccelerator.project.dto.ProjectPhaseDTO;
import com.doveaccelerator.project.entity.Project;
import com.doveaccelerator.project.entity.ProjectMember;
import com.doveaccelerator.project.entity.ProjectPhase;
import com.doveaccelerator.project.mapper.ProjectMapper;
import com.doveaccelerator.project.repository.ProjectRepository;
import com.doveaccelerator.project.service.ProjectService;
import com.doveaccelerator.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Override
    @Transactional
    public ProjectDTO createProject(ProjectDTO projectDTO) {
        Project project = projectMapper.toEntity(projectDTO);
        project = projectRepository.save(project);
        return projectMapper.toDTO(project);
    }

    @Override
    @Transactional
    public ProjectDTO updateProject(Long id, ProjectDTO projectDTO) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new BusinessException("project.not.found"));
        
        projectMapper.updateEntity(projectDTO, project);
        project = projectRepository.save(project);
        return projectMapper.toDTO(project);
    }

    @Override
    @Transactional
    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    @Override
    public ProjectDTO getProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new BusinessException("project.not.found"));
        return projectMapper.toDTO(project);
    }

    @Override
    public Page<ProjectDTO> getAllProjects(Pageable pageable) {
        return projectRepository.findAll(pageable)
                .map(projectMapper::toDTO);
    }

    @Override
    public List<ProjectDTO> getProjectsByStatus(String status) {
        return projectRepository.findByStatus(status).stream()
                .map(projectMapper::toDTO)
                .toList();
    }

    @Override
    public List<ProjectDTO> getProjectsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return projectRepository.findByDateRange(startDate, endDate).stream()
                .map(projectMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public ProjectMemberDTO addMember(Long projectId, ProjectMemberDTO memberDTO) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException("project.not.found"));
        
        ProjectMember member = projectMapper.toMemberEntity(memberDTO);
        member.setProject(project);
        project.getMembers().add(member);
        
        project = projectRepository.save(project);
        return projectMapper.toMemberDTO(member);
    }

    @Override
    @Transactional
    public void removeMember(Long projectId, Long memberId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException("project.not.found"));
        
        project.getMembers().removeIf(member -> member.getId().equals(memberId));
        projectRepository.save(project);
    }

    @Override
    @Transactional
    public ProjectPhaseDTO addPhase(Long projectId, ProjectPhaseDTO phaseDTO) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException("project.not.found"));
        
        ProjectPhase phase = projectMapper.toPhaseEntity(phaseDTO);
        phase.setProject(project);
        project.getPhases().add(phase);
        
        project = projectRepository.save(project);
        return projectMapper.toPhaseDTO(phase);
    }

    @Override
    @Transactional
    public ProjectPhaseDTO updatePhase(Long projectId, Long phaseId, ProjectPhaseDTO phaseDTO) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException("project.not.found"));
        
        ProjectPhase phase = project.getPhases().stream()
                .filter(p -> p.getId().equals(phaseId))
                .findFirst()
                .orElseThrow(() -> new BusinessException("project.phase.not.found"));
        
        projectMapper.updatePhaseEntity(phaseDTO, phase);
        project = projectRepository.save(project);
        return projectMapper.toPhaseDTO(phase);
    }

    @Override
    @Transactional
    public void updateProgress(Long projectId, Long phaseId, Integer progress) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException("project.not.found"));
        
        ProjectPhase phase = project.getPhases().stream()
                .filter(p -> p.getId().equals(phaseId))
                .findFirst()
                .orElseThrow(() -> new BusinessException("project.phase.not.found"));
        
        phase.setProgress(progress);
        projectRepository.save(project);
    }

    @Override
    @Transactional
    public void startProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new BusinessException("project.not.found"));
        project.setStatus("IN_PROGRESS");
        project.setStartTime(LocalDateTime.now());
        projectRepository.save(project);
    }

    @Override
    @Transactional
    public void completeProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new BusinessException("project.not.found"));
        project.setStatus("COMPLETED");
        project.setEndTime(LocalDateTime.now());
        projectRepository.save(project);
    }

    @Override
    @Transactional
    public void pauseProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new BusinessException("project.not.found"));
        project.setStatus("PAUSED");
        projectRepository.save(project);
    }

    @Override
    public List<ProjectDTO> getProjectsByAgent(Long agentId) {
        return projectRepository.findByAgentId(agentId).stream()
                .map(projectMapper::toDTO)
                .toList();
    }
}