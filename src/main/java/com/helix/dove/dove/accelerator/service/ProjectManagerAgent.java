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
            你是一个经验丰富的项目经理AI助手。现在需要你生成一个Prompt，这个Prompt将用于分析以下需求并生成详细的任务列表：
            
            需求描述：%s
            
            请生成一个专业的Prompt，这个Prompt应该：
            1. 包含项目管理的最佳实践
            2. 考虑软件开发生命周期的各个阶段
            3. 确保任务分配合理且覆盖所有必要角色
            4. 包含任务优先级和依赖关系的考虑
            5. 考虑质量保证和风险管理
            6. 确保输出格式符合JSON规范
            
            注意：生成的Prompt必须要求返回的JSON格式包含以下字段：
            - tasks: 任务列表
              - title: 任务标题
              - description: 详细描述
              - role: 执行角色（PRODUCT_MANAGER/ARCHITECT/DEVELOPER/TESTER/OPERATIONS）
              - priority: 优先级（LOW/MEDIUM/HIGH/URGENT）
              - dependencies: 依赖的任务ID列表（可选）
              - estimatedDuration: 预估工时（可选）
            """, requirement);

        logger.debug("正在使用AI生成Prompt...");
        
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
        try {
            String generatedPrompt = providerObj.generateResponse(promptForPromptGeneration, parameters)
                    .get(); // 等待结果
            logger.debug("Prompt生成完成，长度: {} 字符", generatedPrompt.length());
            return generatedPrompt;
        } catch (Exception e) {
            logger.error("生成Prompt失败: {}", e.getMessage(), e);
            throw new RuntimeException("生成Prompt失败: " + e.getMessage());
        }
    }

    @Transactional
    public CompletableFuture<List<Task>> analyzeRequirementAndCreateTasks(Requirement requirement) {
        try {
            logger.info("开始分析需求并创建任务，需求ID: {}", requirement.getId());
            // 更新项目经理状态为工作中
            Agent pmAgent = agentRepository.findById(projectManagerAgent.getId()).orElseThrow();
            pmAgent.setStatus(AgentStatus.WORKING);
            pmAgent.setCurrentTask("分析需求并创建任务");
            agentRepository.save(pmAgent);
            logger.debug("项目经理状态已更新为: {}", pmAgent.getStatus());

            // 第一步：生成用于需求分析的Prompt
            String analysisPrompt = generatePromptForRequirementAnalysis(requirement.getUserBasicRequirement());
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
            String aiResponse = providerObj.generateResponse(analysisPrompt, parameters)
                    .get(); // 等待结果
            logger.debug("AI分析完成，响应长度: {} 字符", aiResponse.length());

            // 解析AI响应
            logger.debug("开始解析AI响应...");
            AITaskResponse taskResponse = objectMapper.readValue(aiResponse, AITaskResponse.class);
            List<Task> createdTasks = new ArrayList<>();
            logger.info("AI分析完成，开始创建任务，任务数量: {}", taskResponse.getTasks().size());

            // 为每个任务创建实体并分配给对应的Agent
            for (AITaskResponse.TaskItem taskItem : taskResponse.getTasks()) {
                logger.debug("正在处理任务: {}", taskItem.getTitle());
                // 查找对应角色的Agent
                AgentRole role = AgentRole.valueOf(taskItem.getRole());
                Agent agent = agentRepository.findByRole(role)
                        .orElseGet(() -> {
                            logger.info("未找到角色 {} 的Agent，正在创建新Agent...", role);
                            Agent newAgent = new Agent();
                            newAgent.setName(role.getDisplayName());
                            newAgent.setRole(role);
                            newAgent.setStatus(AgentStatus.IDLE);
                            return agentRepository.save(newAgent);
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

                Task savedTask = taskRepository.save(task);
                createdTasks.add(savedTask);
                logger.info("已创建任务: {}，ID: {}, 已分配给: {}", 
                    task.getTitle(), savedTask.getId(), agent.getName());

                // 更新Agent状态
                agent.setStatus(AgentStatus.WORKING);
                agent.setCurrentTask(taskItem.getTitle());
                agentRepository.save(agent);
                logger.debug("已更新Agent {} 的状态为: {}", agent.getName(), agent.getStatus());
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
            // 只更新状态相关字段
            Agent agent = agentRepository.findById(projectManagerAgent.getId()).orElseThrow();
            agent.setStatus(AgentStatus.WORKING);
            agent.setCurrentTask("监控任务进度");
            agentRepository.save(agent);

            // 获取所有进行中的任务，使用JOIN FETCH预加载关联实体
            List<Task> activeTasks = taskRepository.findActiveTasksWithAgents();
            logger.info("当前有 {} 个活动任务需要监控", activeTasks.size());
            
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
                String aiResponse = providerObj.generateResponse(monitoringPrompt, parameters)
                        .get(); // 等待结果
                logger.debug("AI分析完成，响应长度: {} 字符", aiResponse.length());
                // TODO: 解析AI响应并更新任务状态
            } else {
                logger.info("没有需要监控的活动任务");
            }

            // 更新状态为空闲
            agent = agentRepository.findById(projectManagerAgent.getId()).orElseThrow();
            agent.setStatus(AgentStatus.IDLE);
            agent.setCurrentTask(null);
            agentRepository.save(agent);
            logger.info("任务监控完成");
        } catch (Exception e) {
            logger.error("任务监控过程中发生错误: {}", e.getMessage(), e);
            logger.error("错误详情: ", e);
            Agent agent = agentRepository.findById(projectManagerAgent.getId()).orElseThrow();
            agent.setStatus(AgentStatus.BLOCKED);
            agent.setCurrentTask("监控错误：" + e.getMessage());
            agentRepository.save(agent);
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