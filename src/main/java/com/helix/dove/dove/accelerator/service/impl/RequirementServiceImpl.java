package com.helix.dove.dove.accelerator.service.impl;

import com.helix.dove.dove.accelerator.dto.RequirementRequest;
import com.helix.dove.dove.accelerator.entity.Requirement;
import com.helix.dove.dove.accelerator.repository.RequirementRepository;
import com.helix.dove.dove.accelerator.service.RequirementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RequirementServiceImpl implements RequirementService {

    private final RequirementRepository requirementRepository;

    @Autowired
    public RequirementServiceImpl(RequirementRepository requirementRepository) {
        this.requirementRepository = requirementRepository;
    }

    @Override
    @Transactional
    public Requirement submitRequirement(RequirementRequest request) {
        Requirement requirement = new Requirement();
        requirement.setUserBasicRequirement(request.getUserBasicRequirement());
        return requirementRepository.save(requirement);
    }

    @Override
    @Transactional(readOnly = true)
    public Requirement getLatestRequirement() {
        return requirementRepository.findAll(
            PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "createdAt"))
        ).stream().findFirst().orElse(null);
    }
} 