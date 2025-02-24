package com.helix.dove.dove.accelerator.service;

import com.helix.dove.dove.accelerator.dto.RequirementRequest;
import com.helix.dove.dove.accelerator.entity.Requirement;

public interface RequirementService {
    Requirement submitRequirement(RequirementRequest request);
    Requirement getLatestRequirement();
} 