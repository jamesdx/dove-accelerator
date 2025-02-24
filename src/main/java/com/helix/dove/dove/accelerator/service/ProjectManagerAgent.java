package com.helix.dove.dove.accelerator.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.helix.dove.dove.accelerator.dto.AITaskResponse;
import com.helix.dove.dove.accelerator.entity.*;
import com.helix.dove.dove.accelerator.event.RequirementEvent;
import com.helix.dove.dove.accelerator.repository.AgentRepository;
import com.helix.dove.dove.accelerator.repository.TaskRepository;
import com.helix.dove.dove.accelerator.service.ai.AIProvider;
import com.helix.dove.dove.accelerator.service.ai.AIProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class ProjectManagerAgent {
    private static final Logger logger = LoggerFactory.getLogger(ProjectManagerAgent.class);

    private final AIProviderFactory aiProviderFactory;
    private final AgentRepository agentRepository;
    private final TaskRepository taskRepository;
    private final Agent projectManagerAgent;
    private final ObjectMapper objectMapper;

    @Autowired
    public ProjectManagerAgent(
            AIProviderFactory aiProviderFactory,
            AgentRepository agentRepository,
            TaskRepository taskRepository,
            ObjectMapper objectMapper) {
        this.aiProviderFactory = aiProviderFactory;
        this.agentRepository = agentRepository;
        this.taskRepository = taskRepository;
        this.objectMapper = objectMapper;
        
        logger.info("正在初始化项目经理AI助手...");
        // 确保项目经理Agent存在
        this.projectManagerAgent = agentRepository.findByRole(AgentRole.PROJECT_MANAGER)
                .orElseGet(() -> {
                    logger.info("未找到项目经理角色，正在创建新的项目经理Agent...");
                    Agent pm = new Agent();
                    pm.setName("项目经理");
                    pm.setRole(AgentRole.PROJECT_MANAGER);
                    pm.setStatus(AgentStatus.BLOCKED);
                    pm.setAiProvider("anthropic");
                    return agentRepository.save(pm);
                });
        
        logger.info("项目经理AI助手初始化完成，当前状态: {}", projectManagerAgent.getStatus());
    }

    @EventListener
    @Async
    public void handleRequirementSubmission(RequirementEvent event) {
        logger.info("收到新的需求提交事件，需求ID: {}", event.getRequirement().getId());
        logger.debug("需求内容: {}", event.getRequirement().getUserBasicRequirement());
        analyzeRequirementAndCreateTasks(event.getRequirement());
    }

    @Scheduled(fixedRate = 300000)
    @Async
    public void scheduleTaskMonitoring() {
        logger.debug("准备执行定时任务监控...");
        if (projectManagerAgent.getStatus() != AgentStatus.WORKING) {
            logger.info("开始执行任务监控...");
            monitorTaskProgress();
        } else {
            logger.debug("项目经理正在处理其他任务，跳过本次监控。当前任务: {}", 
                projectManagerAgent.getCurrentTask());
        }
    }

    private AIProvider getAIProvider(Agent agent) {
        String configuredProvider = agent.getAiProvider();
        logger.debug("获取AI提供商，Agent: {}, 指定提供商: {}", agent.getName(), configuredProvider);
        
        AIProvider provider = aiProviderFactory.getProvider(configuredProvider);
        if (provider == null || !provider.isAvailable()) {
            logger.error("指定的AI提供商 {} 不可用", configuredProvider);
            throw new RuntimeException("指定的AI提供商不可用: " + configuredProvider);
        }
        
        return provider;
    }

    private String generatePromptForRequirementAnalysis(String requirement) {
        logger.debug("开始生成需求分析Prompt...");
        String promptForPromptGeneration = String.format("""
            你是一个经验丰富的项目经理AI助手。我需要你分析以下需求并生成任务列表。
            
            需求描述：%s
            
            请按照软件开发生命周期(SDLC)的各个阶段，生成详细的任务列表。任务应该包括但不限于：
            1. 需求分析阶段的任务
            2. 设计阶段的任务（架构设计、详细设计）
            3. 开发阶段的任务（后端开发、前端开发）
            4. 测试阶段的任务（单元测试、集成测试、系统测试）
            5. 部署和运维阶段的任务
            
            请严格按照以下JSON格式返回任务列表，不要包含任何其他解释性文本：
            {
              "tasks": [
                {
                  "title": "任务标题",
                  "description": "详细描述",
                  "role": "执行角色",
                  "priority": "优先级",
                  "dependencies": [依赖任务的索引],
                  "estimatedDuration": 预估工时
                }
              ]
            }
            
            必须遵守以下规则：
            1. role必须是以下之一：PRODUCT_MANAGER, ARCHITECT, DEVELOPER, TESTER, OPERATIONS
            2. priority必须是以下之一：LOW, MEDIUM, HIGH, URGENT
            3. dependencies是可选的，表示当前任务依赖的其他任务的索引（从0开始）
            4. estimatedDuration表示预估工时（小时）
            5. 每个任务的description应该详细说明任务目标、验收标准和注意事项
            
            重要提示：
            1. 必须返回有效的JSON格式
            2. 不要在JSON之外添加任何解释或分析
            3. 确保所有字段的值都符合上述规则
            4. 确保JSON格式正确，可以被解析
            """, requirement);
            logger.info("生成需求分析Prompt: {}", promptForPromptGeneration);
            return promptForPromptGeneration;
    }

    @Transactional
    public CompletableFuture<List<Task>> analyzeRequirementAndCreateTasks(Requirement requirement) {
        try {
            logger.info("开始分析需求并创建任务，需求ID: {}", requirement.getId());
            logger.info("需求内容: {}", requirement.getUserBasicRequirement());

            // 检查数据库连接
            try {
                long taskCount = taskRepository.count();
                logger.info("当前数据库中的任务总数: {}", taskCount);
            } catch (Exception e) {
                logger.error("数据库连接检查失败: {}", e.getMessage());
                throw new RuntimeException("数据库连接失败", e);
            }

            // 更新项目经理状态为工作中
            Agent pmAgent = agentRepository.findById(projectManagerAgent.getId()).orElseThrow();
            pmAgent.setStatus(AgentStatus.WORKING);
            pmAgent.setCurrentTask("分析需求并创建任务");
            agentRepository.save(pmAgent);
            logger.debug("项目经理状态已更新为: {}", pmAgent.getStatus());

            // 第一步：生成用于需求分析的Prompt
            String prompt = generatePromptForRequirementAnalysis(requirement.getUserBasicRequirement());
            logger.debug("已生成需求分析Prompt，准备调用AI服务...");

            // 第二步：使用生成的Prompt进行需求分析
            logger.info("正在使用AI分析需求...");
            
            // 配置AI调用参数
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("temperature", 0.7);
            
            // 根据 AI 提供商类型设置对应的模型
            String provider = projectManagerAgent.getAiProvider();
            if ("anthropic".equals(provider)) {
                parameters.put("model", "claude-3-opus-20240229");
            } else if ("openai".equals(provider)) {
                parameters.put("model", "gpt-3.5-turbo");
            }

            // 获取项目经理的AI提供商并发送请求
            AIProvider providerObj = getAIProvider(projectManagerAgent);
            String aiResponse = providerObj.generateResponse(prompt, parameters)
                    .get(); // 等待结果
            logger.debug("AI分析完成，响应长度: {} 字符", aiResponse.length());
            logger.debug("AI原始响应内容: {}", aiResponse);

            // 解析AI响应
            logger.debug("开始解析AI响应...");
            AITaskResponse taskResponse;
            try {
                taskResponse = objectMapper.readValue(aiResponse, AITaskResponse.class);
                logger.info("成功解析AI响应为JSON格式");
            } catch (Exception e) {
                logger.error("解析AI响应失败: {}", e.getMessage());
                logger.error("AI响应内容: {}", aiResponse);
                throw new RuntimeException("解析AI响应失败: " + e.getMessage());
            }
            List<Task> createdTasks = new ArrayList<>();
            logger.info("AI分析完成，开始创建任务，任务数量: {}", taskResponse.getTasks().size());

            // 为每个任务创建实体并分配给对应的Agent
            for (AITaskResponse.TaskItem taskItem : taskResponse.getTasks()) {
                logger.info("开始处理任务: {}", taskItem.getTitle());
                logger.debug("任务详情: role={}, priority={}, estimatedDuration={}", 
                    taskItem.getRole(), taskItem.getPriority(), taskItem.getEstimatedDuration());

                // 查找对应角色的Agent
                AgentRole role = AgentRole.valueOf(taskItem.getRole());
                Agent agent = agentRepository.findByRole(role)
                        .orElseGet(() -> {
                            logger.info("未找到角色 {} 的Agent，正在创建新Agent...", role);
                            Agent newAgent = new Agent();
                            newAgent.setName(role.getDisplayName());
                            newAgent.setRole(role);
                            newAgent.setStatus(AgentStatus.IDLE);
                            newAgent = agentRepository.save(newAgent);
                            logger.info("成功创建新Agent: id={}, name={}", newAgent.getId(), newAgent.getName());
                            return newAgent;
                        });

                // 创建新任务
                Task task = new Task();
                task.setTitle(taskItem.getTitle());
                task.setDescription(taskItem.getDescription());
                task.setRequirement(requirement);
                task.setAssignedAgent(agent);
                task.setPriority(TaskPriority.valueOf(taskItem.getPriority()));
                task.setStatus(TaskStatus.PENDING);
                if (taskItem.getEstimatedDuration() != null) {
                    task.setEstimatedHours(taskItem.getEstimatedDuration());
                }

                try {
                    Task savedTask = taskRepository.save(task);
                    createdTasks.add(savedTask);
                    logger.info("成功创建任务: id={}, title={}, assignedTo={}", 
                        savedTask.getId(), savedTask.getTitle(), agent.getName());
                } catch (Exception e) {
                    logger.error("保存任务失败: {}", e.getMessage());
                    throw new RuntimeException("保存任务失败", e);
                }

                // 更新Agent状态
                try {
                    agent.setStatus(AgentStatus.WORKING);
                    agent.setCurrentTask(taskItem.getTitle());
                    agent = agentRepository.save(agent);
                    logger.info("已更新Agent状态: id={}, name={}, status={}, currentTask={}", 
                        agent.getId(), agent.getName(), agent.getStatus(), agent.getCurrentTask());
                } catch (Exception e) {
                    logger.error("更新Agent状态失败: {}", e.getMessage());
                    throw new RuntimeException("更新Agent状态失败", e);
                }
            }

            // 设置任务依赖关系
            logger.debug("开始设置任务依赖关系...");
            for (int i = 0; i < taskResponse.getTasks().size(); i++) {
                AITaskResponse.TaskItem taskItem = taskResponse.getTasks().get(i);
                if (taskItem.getDependencies() != null && !taskItem.getDependencies().isEmpty()) {
                    Task task = createdTasks.get(i);
                    for (Long dependencyId : taskItem.getDependencies()) {
                        task.getDependencies().add(createdTasks.get(dependencyId.intValue()));
                        logger.debug("为任务 {} 添加依赖任务 {}", 
                            task.getTitle(), createdTasks.get(dependencyId.intValue()).getTitle());
                    }
                    taskRepository.save(task);
                }
            }

            // 更新项目经理状态为空闲
            pmAgent = agentRepository.findById(projectManagerAgent.getId()).orElseThrow();
            pmAgent.setStatus(AgentStatus.IDLE);
            pmAgent.setCurrentTask(null);
            agentRepository.save(pmAgent);
            logger.info("需求分析和任务创建完成，共创建了 {} 个任务", createdTasks.size());

            return CompletableFuture.completedFuture(createdTasks);
        } catch (Exception e) {
            logger.error("需求分析过程中发生错误: {}", e.getMessage(), e);
            logger.error("错误详情: ", e);
            Agent pmAgent = agentRepository.findById(projectManagerAgent.getId()).orElseThrow();
            pmAgent.setStatus(AgentStatus.BLOCKED);
            pmAgent.setCurrentTask("错误：" + e.getMessage());
            agentRepository.save(pmAgent);
            return CompletableFuture.failedFuture(e);
        }
    }

    @Transactional
    public void monitorTaskProgress() {
        try {
            logger.info("开始执行任务监控...");
            
            // 检查数据库连接
            try {
                long activeTaskCount = taskRepository.countByStatusNot(TaskStatus.COMPLETED);
                logger.info("当前活动任务数量: {}", activeTaskCount);
            } catch (Exception e) {
                logger.error("检查活动任务数量失败: {}", e.getMessage());
                throw new RuntimeException("数据库查询失败", e);
            }

            // 更新项目经理状态
            Agent agent = agentRepository.findById(projectManagerAgent.getId()).orElseThrow();
            agent.setStatus(AgentStatus.WORKING);
            agent.setCurrentTask("监控任务进度");
            try {
                agent = agentRepository.save(agent);
                logger.info("已更新项目经理状态: status={}, currentTask={}", 
                    agent.getStatus(), agent.getCurrentTask());
            } catch (Exception e) {
                logger.error("更新项目经理状态失败: {}", e.getMessage());
                throw new RuntimeException("更新项目经理状态失败", e);
            }

            // 获取所有进行中的任务
            List<Task> activeTasks;
            try {
                activeTasks = taskRepository.findActiveTasksWithAgents();
                logger.info("成功获取活动任务列表，任务数量: {}", activeTasks.size());
                
                // 记录每个任务的详细信息
                for (Task task : activeTasks) {
                    logger.info("活动任务详情: id={}, title={}, status={}, assignedTo={}, progress={}%", 
                        task.getId(), task.getTitle(), task.getStatus(),
                        task.getAssignedAgent().getName(),
                        task.getActualHours() != null && task.getEstimatedHours() != null ? 
                            (task.getActualHours() * 100 / task.getEstimatedHours()) : 0);
                }
            } catch (Exception e) {
                logger.error("获取活动任务列表失败: {}", e.getMessage());
                throw new RuntimeException("获取任务列表失败", e);
            }

            // 使用AI分析任务进度
            if (!activeTasks.isEmpty()) {
                logger.debug("开始生成任务监控Prompt...");
                String monitoringPrompt = generateTaskMonitoringPrompt(activeTasks);
                
                // 配置AI调用参数
                Map<String, Object> parameters = new HashMap<>();
                parameters.put("temperature", 0.7);
                
                // 根据 AI 提供商类型设置对应的模型
                String provider = projectManagerAgent.getAiProvider();
                if ("anthropic".equals(provider)) {
                    parameters.put("model", "claude-3-opus-20240229");
                } else if ("openai".equals(provider)) {
                    parameters.put("model", "gpt-3.5-turbo");
                }

                // 获取项目经理的AI提供商并发送请求
                logger.debug("正在使用AI分析任务进度...");
                AIProvider providerObj = getAIProvider(projectManagerAgent);
                try {
                    String aiResponse = providerObj.generateResponse(monitoringPrompt, parameters)
                            .get(); // 等待结果
                    logger.debug("AI分析完成，响应长度: {} 字符", aiResponse.length());
                    // TODO: 解析AI响应并更新任务状态
                } catch (InterruptedException e) {
                    logger.error("AI分析任务被中断: {}", e.getMessage());
                    Thread.currentThread().interrupt(); // 重新设置中断标志
                    throw new RuntimeException("AI分析任务被中断", e);
                } catch (ExecutionException e) {
                    logger.error("AI分析任务执行失败: {}", e.getMessage());
                    throw new RuntimeException("AI分析任务执行失败", e);
                }
            } else {
                logger.info("没有需要监控的活动任务");
            }

            // 更新项目经理状态为空闲
            try {
                agent = agentRepository.findById(projectManagerAgent.getId()).orElseThrow();
                agent.setStatus(AgentStatus.IDLE);
                agent.setCurrentTask(null);
                agent = agentRepository.save(agent);
                logger.info("任务监控完成，项目经理已恢复空闲状态");
            } catch (Exception e) {
                logger.error("更新项目经理状态失败: {}", e.getMessage());
                throw new RuntimeException("更新项目经理状态失败", e);
            }
        } catch (Exception e) {
            logger.error("任务监控过程中发生错误: {}", e.getMessage(), e);
            try {
                Agent agent = agentRepository.findById(projectManagerAgent.getId()).orElseThrow();
                agent.setStatus(AgentStatus.BLOCKED);
                agent.setCurrentTask("监控错误：" + e.getMessage());
                agentRepository.save(agent);
                logger.info("已将项目经理状态更新为已阻塞");
            } catch (Exception ex) {
                logger.error("更新项目经理错误状态失败: {}", ex.getMessage());
            }
            throw new RuntimeException("任务监控失败", e);
        }
    }

    private String generateTaskMonitoringPrompt(List<Task> tasks) {
        logger.debug("开始生成任务监控Prompt，任务数量: {}", tasks.size());
        StringBuilder tasksInfo = new StringBuilder();
        for (Task task : tasks) {
            tasksInfo.append(String.format("""
                任务ID: %d
                标题: %s
                描述: %s
                执行人: %s
                当前状态: %s
                优先级: %s
                预估工时: %d小时
                已用工时: %d小时
                
                """,
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getAssignedAgent().getName(),
                task.getStatus(),
                task.getPriority(),
                task.getEstimatedHours() != null ? task.getEstimatedHours() : 0,
                task.getActualHours() != null ? task.getActualHours() : 0
            ));
        }

        String prompt = String.format("""
            作为项目经理，请分析以下任务的执行情况：
            
            %s
            
            请对每个任务进行分析，考虑：
            1. 任务是否需要调整优先级
            2. 是否存在被阻塞的风险
            3. 是否需要分配更多资源
            4. 是否需要调整时间预估
            
            请以JSON格式返回分析结果：
            {
                "taskAnalysis": [
                    {
                        "taskId": 任务ID,
                        "status": "IN_PROGRESS/BLOCKED/COMPLETED",
                        "adjustedPriority": "LOW/MEDIUM/HIGH/URGENT",
                        "recommendation": "具体建议",
                        "riskLevel": "LOW/MEDIUM/HIGH"
                    }
                ]
            }
            """, tasksInfo.toString());

        logger.debug("任务监控Prompt生成完成，长度: {} 字符", prompt.length());
        return prompt;
    }
} 