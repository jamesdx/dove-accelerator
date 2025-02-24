package com.helix.dove.dove.accelerator.service;

import com.helix.dove.dove.accelerator.dto.RequirementRequest;
import com.helix.dove.dove.accelerator.dto.RequirementResponse;

public interface RequirementService {
    RequirementResponse submitRequirement(RequirementRequest request);
} 