package com.helix.dove.dove.accelerator.service.impl;

import com.helix.dove.dove.accelerator.dto.RequirementRequest;
import com.helix.dove.dove.accelerator.dto.RequirementResponse;
import com.helix.dove.dove.accelerator.entity.Requirement;
import com.helix.dove.dove.accelerator.repository.RequirementRepository;
import com.helix.dove.dove.accelerator.service.RequirementService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public RequirementResponse submitRequirement(RequirementRequest request) {
        // 创建新的需求实体
        Requirement requirement = new Requirement();
        requirement.setDescription(request.description());
        
        // 保存到数据库
        requirementRepository.save(requirement);

        // 返回响应
        return RequirementResponse.success(
            "需求已接收",
            "您的需求已成功记录，我们会尽快处理"
        );
    }
} 