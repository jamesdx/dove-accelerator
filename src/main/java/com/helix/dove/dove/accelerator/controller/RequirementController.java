package com.helix.dove.dove.accelerator.controller;

import com.helix.dove.dove.accelerator.dto.RequirementRequest;
import com.helix.dove.dove.accelerator.dto.RequirementResponse;
import com.helix.dove.dove.accelerator.entity.Requirement;
import com.helix.dove.dove.accelerator.entity.Task;
import com.helix.dove.dove.accelerator.event.RequirementEvent;
import com.helix.dove.dove.accelerator.service.RequirementService;
import com.helix.dove.dove.accelerator.repository.TaskRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/requirements")
public class RequirementController {

    private final RequirementService requirementService;
    private final ApplicationEventPublisher eventPublisher;
    private final TaskRepository taskRepository;

    @Autowired
    public RequirementController(
            RequirementService requirementService,
            ApplicationEventPublisher eventPublisher,
            TaskRepository taskRepository) {
        this.requirementService = requirementService;
        this.eventPublisher = eventPublisher;
        this.taskRepository = taskRepository;
    }

    @PostMapping
    public ResponseEntity<RequirementResponse> submitRequirement(@Valid @RequestBody RequirementRequest request) {
        try {
            // 保存需求
            Requirement requirement = requirementService.submitRequirement(request);
            
            // 发布需求事件
            eventPublisher.publishEvent(new RequirementEvent(this, requirement));

            return ResponseEntity.ok(RequirementResponse.success(
                "需求已接收",
                "您的需求已成功提交，项目经理AI助手将立即开始分析并创建任务清单"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(RequirementResponse.error("错误", e.getMessage()));
        }
    }

    @GetMapping("/latest/tasks")
    public ResponseEntity<?> getLatestRequirementTasks() {
        try {
            // 获取最新的需求
            Requirement latestRequirement = requirementService.getLatestRequirement();
            if (latestRequirement == null) {
                return ResponseEntity.notFound().build();
            }

            // 获取该需求的所有任务
            List<Task> tasks = taskRepository.findByRequirement(latestRequirement);
            if (tasks.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                    "message", "暂无任务",
                    "requirementId", latestRequirement.getId()
                ));
            }

            // 转换为响应格式
            List<Map<String, Object>> taskList = tasks.stream()
                .map(task -> Map.of(
                    "id", task.getId(),
                    "title", task.getTitle(),
                    "description", task.getDescription(),
                    "status", task.getStatus(),
                    "priority", task.getPriority(),
                    "assignedAgent", Map.of(
                        "id", task.getAssignedAgent().getId(),
                        "name", task.getAssignedAgent().getName(),
                        "role", task.getAssignedAgent().getRole()
                    ),
                    "estimatedHours", task.getEstimatedHours() != null ? task.getEstimatedHours() : 0,
                    "actualHours", task.getActualHours() != null ? task.getActualHours() : 0
                ))
                .toList();

            return ResponseEntity.ok(Map.of(
                "requirementId", latestRequirement.getId(),
                "requirement", latestRequirement.getUserBasicRequirement(),
                "tasks", taskList
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }
} 