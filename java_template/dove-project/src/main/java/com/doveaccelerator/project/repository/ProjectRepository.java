package com.doveaccelerator.project.repository;

import com.doveaccelerator.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findByName(String name);
    
    List<Project> findByStatus(String status);
    
    @Query("SELECT p FROM Project p WHERE p.startTime >= :startDate AND p.startTime <= :endDate")
    List<Project> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT p FROM Project p JOIN p.members m WHERE m.agentId = :agentId AND m.active = true")
    List<Project> findByAgentId(Long agentId);
    
    @Query("SELECT p FROM Project p JOIN p.phases ph WHERE ph.status = :phaseStatus")
    List<Project> findByPhaseStatus(String phaseStatus);
}