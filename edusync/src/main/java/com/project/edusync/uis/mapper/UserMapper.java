package com.project.edusync.uis.mapper;

import com.project.edusync.iam.model.dto.CreateUserRequestDTO;
import com.project.edusync.iam.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    // Map basic identity fields
    @Mapping(target = "username", source = "username")
    @Mapping(target = "email", source = "email")
    // Password is set manually in service after encoding
    // Roles are set manually in service
    // isActive is true by default or set manually
    User toEntity(CreateUserRequestDTO dto);
}