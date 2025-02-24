package com.helix.dove.dove.accelerator.controller;

import com.helix.dove.dove.accelerator.dto.RequirementRequest;
import com.helix.dove.dove.accelerator.dto.RequirementResponse;
import com.helix.dove.dove.accelerator.service.RequirementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/requirements")
public class RequirementController {

    private final RequirementService requirementService;

    @Autowired
    public RequirementController(RequirementService requirementService) {
        this.requirementService = requirementService;
    }

    @PostMapping
    public ResponseEntity<RequirementResponse> submitRequirement(@Valid @RequestBody RequirementRequest request) {
        try {
            RequirementResponse response = requirementService.submitRequirement(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(RequirementResponse.error("错误", e.getMessage()));
        }
    }
} 