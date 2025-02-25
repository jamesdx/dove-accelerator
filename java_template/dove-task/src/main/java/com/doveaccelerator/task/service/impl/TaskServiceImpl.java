package com.doveaccelerator.task.service.impl;

import com.doveaccelerator.task.dto.TaskDTO;
import com.doveaccelerator.task.dto.TaskAssignmentDTO;
import com.doveaccelerator.task.dto.TaskDependencyDTO;
import com.doveaccelerator.task.entity.Task;
import com.doveaccelerator.task.entity.TaskAssignment;
import com.doveaccelerator.task.entity.TaskDependency;
import com.doveaccelerator.task.mapper.TaskMapper;
import com.doveaccelerator.task.repository.TaskRepository;
import com.doveaccelerator.task.service.TaskService;
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
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Override
    @Transactional
    public TaskDTO createTask(TaskDTO taskDTO) {
        Task task = taskMapper.toEntity(taskDTO);
        task = taskRepository.save(task);
        return taskMapper.toDTO(task);
    }

    @Override
    @Transactional
    public TaskDTO updateTask(Long id, TaskDTO taskDTO) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new BusinessException("task.not.found"));
        
        taskMapper.updateEntity(taskDTO, task);
        task = taskRepository.save(task);
        return taskMapper.toDTO(task);
    }

    @Override
    @Transactional
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    @Override
    public TaskDTO getTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new BusinessException("task.not.found"));
        return taskMapper.toDTO(task);
    }

    @Override
    public Page<TaskDTO> getAllTasks(Pageable pageable) {
        return taskRepository.findAll(pageable)
                .map(taskMapper::toDTO);
    }

    @Override
    public List<TaskDTO> getTasksByProject(Long projectId) {
        return taskRepository.findByProjectId(projectId).stream()
                .map(taskMapper::toDTO)
                .toList();
    }

    @Override
    public List<TaskDTO> getTasksByProjectAndStatus(Long projectId, String status) {
        return taskRepository.findByProjectIdAndStatus(projectId, status).stream()
                .map(taskMapper::toDTO)
                .toList();
    }

    @Override
    public List<TaskDTO> getTasksByPhase(Long phaseId) {
        return taskRepository.findByPhaseId(phaseId).stream()
                .map(taskMapper::toDTO)
                .toList();
    }

    @Override
    public List<TaskDTO> getOverdueTasks(LocalDateTime deadline) {
        return taskRepository.findOverdueTasks(deadline).stream()
                .map(taskMapper::toDTO)
                .toList();
    }

    @Override
    public List<TaskDTO> getTasksByAgent(Long agentId) {
        return taskRepository.findByAssignedAgent(agentId).stream()
                .map(taskMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public TaskAssignmentDTO assignTask(Long taskId, TaskAssignmentDTO assignmentDTO) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new BusinessException("task.not.found"));
        
        TaskAssignment assignment = taskMapper.toAssignmentEntity(assignmentDTO);
        assignment.setTask(task);
        task.getAssignments().add(assignment);
        
        task = taskRepository.save(task);
        return taskMapper.toAssignmentDTO(assignment);
    }

    @Override
    @Transactional
    public void removeAssignment(Long taskId, Long assignmentId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new BusinessException("task.not.found"));
        
        task.getAssignments().removeIf(assignment -> assignment.getId().equals(assignmentId));
        taskRepository.save(task);
    }

    @Override
    @Transactional
    public TaskDependencyDTO addDependency(TaskDependencyDTO dependencyDTO) {
        Task sourceTask = taskRepository.findById(dependencyDTO.getSourceTaskId())
                .orElseThrow(() -> new BusinessException("task.source.not.found"));
        
        TaskDependency dependency = taskMapper.toDependencyEntity(dependencyDTO);
        dependency.setSourceTask(sourceTask);
        sourceTask.getDependencies().add(dependency);
        
        sourceTask = taskRepository.save(sourceTask);
        return taskMapper.toDependencyDTO(dependency);
    }

    @Override
    @Transactional
    public void removeDependency(Long dependencyId) {
        // The dependency will be automatically removed from the source task's dependencies
        // due to the CascadeType.ALL configuration
        taskRepository.findAll().forEach(task -> {
            task.getDependencies().removeIf(dep -> dep.getId().equals(dependencyId));
            taskRepository.save(task);
        });
    }

    @Override
    @Transactional
    public void updateProgress(Long taskId, Integer progress) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new BusinessException("task.not.found"));
        task.setProgress(progress);
        taskRepository.save(task);
    }

    @Override
    @Transactional
    public void startTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new BusinessException("task.not.found"));
        task.setStatus("IN_PROGRESS");
        task.setStartTime(LocalDateTime.now());
        taskRepository.save(task);
    }

    @Override
    @Transactional
    public void completeTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new BusinessException("task.not.found"));
        task.setStatus("COMPLETED");
        task.setProgress(100);
        task.setEndTime(LocalDateTime.now());
        taskRepository.save(task);
    }

    @Override
    @Transactional
    public void pauseTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new BusinessException("task.not.found"));
        task.setStatus("PAUSED");
        taskRepository.save(task);
    }
}