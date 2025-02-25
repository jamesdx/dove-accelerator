package com.doveaccelerator.task.controller;

import com.doveaccelerator.task.dto.TaskDTO;
import com.doveaccelerator.task.dto.TaskAssignmentDTO;
import com.doveaccelerator.task.dto.TaskDependencyDTO;
import com.doveaccelerator.task.service.TaskService;
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
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ApiResponse<TaskDTO> createTask(@Valid @RequestBody TaskDTO taskDTO) {
        return ApiResponse.success(taskService.createTask(taskDTO));
    }

    @PutMapping("/{id}")
    public ApiResponse<TaskDTO> updateTask(@PathVariable Long id, @Valid @RequestBody TaskDTO taskDTO) {
        return ApiResponse.success(taskService.updateTask(id, taskDTO));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/{id}")
    public ApiResponse<TaskDTO> getTask(@PathVariable Long id) {
        return ApiResponse.success(taskService.getTask(id));
    }

    @GetMapping
    public ApiResponse<Page<TaskDTO>> getAllTasks(Pageable pageable) {
        return ApiResponse.success(taskService.getAllTasks(pageable));
    }

    @GetMapping("/project/{projectId}")
    public ApiResponse<List<TaskDTO>> getTasksByProject(@PathVariable Long projectId) {
        return ApiResponse.success(taskService.getTasksByProject(projectId));
    }

    @GetMapping("/project/{projectId}/status/{status}")
    public ApiResponse<List<TaskDTO>> getTasksByProjectAndStatus(
            @PathVariable Long projectId,
            @PathVariable String status) {
        return ApiResponse.success(taskService.getTasksByProjectAndStatus(projectId, status));
    }

    @GetMapping("/phase/{phaseId}")
    public ApiResponse<List<TaskDTO>> getTasksByPhase(@PathVariable Long phaseId) {
        return ApiResponse.success(taskService.getTasksByPhase(phaseId));
    }

    @GetMapping("/overdue")
    public ApiResponse<List<TaskDTO>> getOverdueTasks(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime deadline) {
        return ApiResponse.success(taskService.getOverdueTasks(deadline));
    }

    @GetMapping("/agent/{agentId}")
    public ApiResponse<List<TaskDTO>> getTasksByAgent(@PathVariable Long agentId) {
        return ApiResponse.success(taskService.getTasksByAgent(agentId));
    }

    @PostMapping("/{taskId}/assignments")
    public ApiResponse<TaskAssignmentDTO> assignTask(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskAssignmentDTO assignmentDTO) {
        return ApiResponse.success(taskService.assignTask(taskId, assignmentDTO));
    }

    @DeleteMapping("/{taskId}/assignments/{assignmentId}")
    public ApiResponse<Void> removeAssignment(
            @PathVariable Long taskId,
            @PathVariable Long assignmentId) {
        taskService.removeAssignment(taskId, assignmentId);
        return ApiResponse.success(null);
    }

    @PostMapping("/dependencies")
    public ApiResponse<TaskDependencyDTO> addDependency(
            @Valid @RequestBody TaskDependencyDTO dependencyDTO) {
        return ApiResponse.success(taskService.addDependency(dependencyDTO));
    }

    @DeleteMapping("/dependencies/{dependencyId}")
    public ApiResponse<Void> removeDependency(@PathVariable Long dependencyId) {
        taskService.removeDependency(dependencyId);
        return ApiResponse.success(null);
    }

    @PutMapping("/{id}/progress")
    public ApiResponse<Void> updateProgress(
            @PathVariable Long id,
            @RequestParam Integer progress) {
        taskService.updateProgress(id, progress);
        return ApiResponse.success(null);
    }

    @PutMapping("/{id}/start")
    public ApiResponse<Void> startTask(@PathVariable Long id) {
        taskService.startTask(id);
        return ApiResponse.success(null);
    }

    @PutMapping("/{id}/complete")
    public ApiResponse<Void> completeTask(@PathVariable Long id) {
        taskService.completeTask(id);
        return ApiResponse.success(null);
    }

    @PutMapping("/{id}/pause")
    public ApiResponse<Void> pauseTask(@PathVariable Long id) {
        taskService.pauseTask(id);
        return ApiResponse.success(null);
    }
}