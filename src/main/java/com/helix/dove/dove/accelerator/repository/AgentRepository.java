package com.helix.dove.dove.accelerator.repository;

import com.helix.dove.dove.accelerator.entity.Agent;
import com.helix.dove.dove.accelerator.entity.AgentRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AgentRepository extends JpaRepository<Agent, Long> {
    Optional<Agent> findByRole(AgentRole role);
} 