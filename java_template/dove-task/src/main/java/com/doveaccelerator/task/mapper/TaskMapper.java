package com.doveaccelerator.task.mapper;

import com.doveaccelerator.task.dto.TaskDTO;
import com.doveaccelerator.task.dto.TaskAssignmentDTO;
import com.doveaccelerator.task.dto.TaskDependencyDTO;
import com.doveaccelerator.task.entity.Task;
import com.doveaccelerator.task.entity.TaskAssignment;
import com.doveaccelerator.task.entity.TaskDependency;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {
    
    @Mapping(target = "assignments", ignore = true)
    @Mapping(target = "dependencies", ignore = true)
    Task toEntity(TaskDTO dto);

    @Mapping(target = "assignments", ignore = true)
    @Mapping(target = "dependencies", ignore = true)
    TaskDTO toDTO(Task entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "assignments", ignore = true)
    @Mapping(target = "dependencies", ignore = true)
    void updateEntity(TaskDTO dto, @MappingTarget Task entity);

    @Mapping(target = "task", ignore = true)
    TaskAssignment toAssignmentEntity(TaskAssignmentDTO dto);

    @Mapping(target = "taskId", source = "task.id")
    TaskAssignmentDTO toAssignmentDTO(TaskAssignment entity);

    @Mapping(target = "sourceTask", ignore = true)
    TaskDependency toDependencyEntity(TaskDependencyDTO dto);

    @Mapping(target = "sourceTaskId", source = "sourceTask.id")
    TaskDependencyDTO toDependencyDTO(TaskDependency entity);
}