package com.synq.backend.mapper;

import com.synq.backend.dto.response.RoleDto;
import com.synq.backend.model.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleDto toDto(Role role);
}

