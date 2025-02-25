package com.doveaccelerator.project.controller;

import com.doveaccelerator.project.dto.ProjectDTO;
import com.doveaccelerator.project.dto.ProjectMemberDTO;
import com.doveaccelerator.project.dto.ProjectPhaseDTO;
import com.doveaccelerator.project.service.ProjectService;
import com.doveaccelerator.common.model.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ApiResponse<ProjectDTO> createProject(@Valid @RequestBody ProjectDTO projectDTO) {
        return ApiResponse.success(projectService.createProject(projectDTO));
    }

    @PutMapping("/{id}")
    public ApiResponse<ProjectDTO> updateProject(@PathVariable Long id, @Valid @RequestBody ProjectDTO projectDTO) {
        return ApiResponse.success(projectService.updateProject(id, projectDTO));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/{id}")
    public ApiResponse<ProjectDTO> getProject(@PathVariable Long id) {
        return ApiResponse.success(projectService.getProject(id));
    }

    @GetMapping
    public ApiResponse<Page<ProjectDTO>> getAllProjects(Pageable pageable) {
        return ApiResponse.success(projectService.getAllProjects(pageable));
    }

    @GetMapping("/status/{status}")
    public ApiResponse<List<ProjectDTO>> getProjectsByStatus(@PathVariable String status) {
        return ApiResponse.success(projectService.getProjectsByStatus(status));
    }

    @GetMapping("/date-range")
    public ApiResponse<List<ProjectDTO>> getProjectsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ApiResponse.success(projectService.getProjectsByDateRange(startDate, endDate));
    }

    @PostMapping("/{projectId}/members")
    public ApiResponse<ProjectMemberDTO> addMember(
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectMemberDTO memberDTO) {
        return ApiResponse.success(projectService.addMember(projectId, memberDTO));
    }

    @DeleteMapping("/{projectId}/members/{memberId}")
    public ApiResponse<Void> removeMember(@PathVariable Long projectId, @PathVariable Long memberId) {
        projectService.removeMember(projectId, memberId);
        return ApiResponse.success(null);
    }

    @PostMapping("/{projectId}/phases")
    public ApiResponse<ProjectPhaseDTO> addPhase(
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectPhaseDTO phaseDTO) {
        return ApiResponse.success(projectService.addPhase(projectId, phaseDTO));
    }

    @PutMapping("/{projectId}/phases/{phaseId}")
    public ApiResponse<ProjectPhaseDTO> updatePhase(
            @PathVariable Long projectId,
            @PathVariable Long phaseId,
            @Valid @RequestBody ProjectPhaseDTO phaseDTO) {
        return ApiResponse.success(projectService.updatePhase(projectId, phaseId, phaseDTO));
    }

    @PutMapping("/{projectId}/phases/{phaseId}/progress")
    public ApiResponse<Void> updateProgress(
            @PathVariable Long projectId,
            @PathVariable Long phaseId,
            @RequestParam Integer progress) {
        projectService.updateProgress(projectId, phaseId, progress);
        return ApiResponse.success(null);
    }

    @PutMapping("/{id}/start")
    public ApiResponse<Void> startProject(@PathVariable Long id) {
        projectService.startProject(id);
        return ApiResponse.success(null);
    }

    @PutMapping("/{id}/complete")
    public ApiResponse<Void> completeProject(@PathVariable Long id) {
        projectService.completeProject(id);
        return ApiResponse.success(null);
    }

    @PutMapping("/{id}/pause")
    public ApiResponse<Void> pauseProject(@PathVariable Long id) {
        projectService.pauseProject(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/agent/{agentId}")
    public ApiResponse<List<ProjectDTO>> getProjectsByAgent(@PathVariable Long agentId) {
        return ApiResponse.success(projectService.getProjectsByAgent(agentId));
    }
}