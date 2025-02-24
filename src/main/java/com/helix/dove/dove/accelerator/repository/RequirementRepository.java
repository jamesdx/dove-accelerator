package com.helix.dove.dove.accelerator.repository;

import com.helix.dove.dove.accelerator.entity.Requirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequirementRepository extends JpaRepository<Requirement, Long> {
} 