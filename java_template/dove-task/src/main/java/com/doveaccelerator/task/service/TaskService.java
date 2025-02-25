package com.doveaccelerator.task.service;

import com.doveaccelerator.task.dto.TaskDTO;
import com.doveaccelerator.task.dto.TaskAssignmentDTO;
import com.doveaccelerator.task.dto.TaskDependencyDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

public interface TaskService {
    TaskDTO createTask(TaskDTO taskDTO);
    
    TaskDTO updateTask(Long id, TaskDTO taskDTO);
    
    void deleteTask(Long id);
    
    TaskDTO getTask(Long id);
    
    Page<TaskDTO> getAllTasks(Pageable pageable);
    
    List<TaskDTO> getTasksByProject(Long projectId);
    
    List<TaskDTO> getTasksByProjectAndStatus(Long projectId, String status);
    
    List<TaskDTO> getTasksByPhase(Long phaseId);
    
    List<TaskDTO> getOverdueTasks(LocalDateTime deadline);
    
    List<TaskDTO> getTasksByAgent(Long agentId);
    
    TaskAssignmentDTO assignTask(Long taskId, TaskAssignmentDTO assignmentDTO);
    
    void removeAssignment(Long taskId, Long assignmentId);
    
    TaskDependencyDTO addDependency(TaskDependencyDTO dependencyDTO);
    
    void removeDependency(Long dependencyId);
    
    void updateProgress(Long taskId, Integer progress);
    
    void startTask(Long id);
    
    void completeTask(Long id);
    
    void pauseTask(Long id);
}