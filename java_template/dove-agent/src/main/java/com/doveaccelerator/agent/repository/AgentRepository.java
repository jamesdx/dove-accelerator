package com.doveaccelerator.agent.repository;

import com.doveaccelerator.agent.entity.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AgentRepository extends JpaRepository<Agent, Long> {
    Optional<Agent> findByName(String name);
    
    List<Agent> findByModelType(String modelType);
    
    List<Agent> findByStatus(String status);
    
    @Query("SELECT a FROM Agent a JOIN a.roles r WHERE r.roleName = :roleName AND r.active = true")
    List<Agent> findByRoleName(String roleName);
    
    @Query("SELECT a FROM Agent a JOIN a.skills s WHERE s.skillName = :skillName AND s.proficiencyLevel >= :minLevel")
    List<Agent> findBySkillAndMinLevel(String skillName, Integer minLevel);
}