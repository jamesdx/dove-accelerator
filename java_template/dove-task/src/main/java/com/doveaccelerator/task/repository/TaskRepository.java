package com.doveaccelerator.task.repository;

import com.doveaccelerator.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProjectId(Long projectId);
    
    List<Task> findByProjectIdAndStatus(Long projectId, String status);
    
    List<Task> findByPhaseId(Long phaseId);
    
    @Query("SELECT t FROM Task t WHERE t.deadline < :deadline AND t.status != 'COMPLETED'")
    List<Task> findOverdueTasks(LocalDateTime deadline);
    
    @Query("SELECT t FROM Task t JOIN t.assignments a WHERE a.agentId = :agentId")
    List<Task> findByAssignedAgent(Long agentId);
    
    @Query("SELECT t FROM Task t WHERE t.projectId = :projectId AND t.progress < 100 ORDER BY t.priority DESC")
    List<Task> findIncompleteTasksByProjectId(Long projectId);
}