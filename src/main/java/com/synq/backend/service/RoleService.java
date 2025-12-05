package com.synq.backend.service;

import com.synq.backend.dto.response.RoleDto;
import com.synq.backend.enums.UserRole;
import com.synq.backend.model.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {

    List<RoleDto> getAllRoles();

    Optional<RoleDto> getRoleByName(UserRole name);

    Optional<Role> getRoleEntityByName(UserRole name);

    void initializeDefaultRoles();
}



