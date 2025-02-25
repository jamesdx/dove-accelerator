package com.doveaccelerator.knowledge.mapper;

import com.doveaccelerator.knowledge.dto.KnowledgeDTO;
import com.doveaccelerator.knowledge.dto.KnowledgeCategoryDTO;
import com.doveaccelerator.knowledge.dto.KnowledgeTagDTO;
import com.doveaccelerator.knowledge.entity.Knowledge;
import com.doveaccelerator.knowledge.entity.KnowledgeCategory;
import com.doveaccelerator.knowledge.entity.KnowledgeTag;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface KnowledgeMapper {
    
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "tagIds", ignore = true)
    KnowledgeDTO toDTO(Knowledge entity);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "tags", ignore = true)
    Knowledge toEntity(KnowledgeDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "tags", ignore = true)
    void updateEntity(KnowledgeDTO dto, @MappingTarget Knowledge entity);

    KnowledgeCategoryDTO toCategoryDTO(KnowledgeCategory entity);

    KnowledgeTagDTO toTagDTO(KnowledgeTag entity);
}