package com.doveaccelerator.project.service;

import com.doveaccelerator.project.dto.ProjectDTO;
import com.doveaccelerator.project.dto.ProjectMemberDTO;
import com.doveaccelerator.project.dto.ProjectPhaseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

public interface ProjectService {
    ProjectDTO createProject(ProjectDTO projectDTO);
    
    ProjectDTO updateProject(Long id, ProjectDTO projectDTO);
    
    void deleteProject(Long id);
    
    ProjectDTO getProject(Long id);
    
    Page<ProjectDTO> getAllProjects(Pageable pageable);
    
    List<ProjectDTO> getProjectsByStatus(String status);
    
    List<ProjectDTO> getProjectsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    ProjectMemberDTO addMember(Long projectId, ProjectMemberDTO memberDTO);
    
    void removeMember(Long projectId, Long memberId);
    
    ProjectPhaseDTO addPhase(Long projectId, ProjectPhaseDTO phaseDTO);
    
    ProjectPhaseDTO updatePhase(Long projectId, Long phaseId, ProjectPhaseDTO phaseDTO);
    
    void updateProgress(Long projectId, Long phaseId, Integer progress);
    
    void startProject(Long id);
    
    void completeProject(Long id);
    
    void pauseProject(Long id);
    
    List<ProjectDTO> getProjectsByAgent(Long agentId);
}