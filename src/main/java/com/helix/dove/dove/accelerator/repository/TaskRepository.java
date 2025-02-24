package com.helix.dove.dove.accelerator.repository;

import com.helix.dove.dove.accelerator.entity.Task;
import com.helix.dove.dove.accelerator.entity.TaskStatus;
import com.helix.dove.dove.accelerator.entity.Requirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByRequirement(Requirement requirement);
    List<Task> findByStatusNot(TaskStatus status);
    long countByStatusNot(TaskStatus status);
    
    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.assignedAgent WHERE t.status != 'COMPLETED'")
    List<Task> findActiveTasksWithAgents();
} 