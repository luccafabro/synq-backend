package com.synq.backend.service.impl;

import com.synq.backend.dto.response.RoleDto;
import com.synq.backend.enums.UserRole;
import com.synq.backend.mapper.RoleMapper;
import com.synq.backend.model.Role;
import com.synq.backend.repository.RoleRepository;
import com.synq.backend.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of RoleService
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    @Transactional(readOnly = true)
    public List<RoleDto> getAllRoles() {
        log.debug("Fetching all roles");
        return roleRepository.findAll().stream()
                .map(roleMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RoleDto> getRoleByName(UserRole name) {
        log.debug("Fetching role by name: {}", name);
        return roleRepository.findByName(name)
                .map(roleMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> getRoleEntityByName(UserRole name) {
        return roleRepository.findByName(name);
    }

    @Override
    public void initializeDefaultRoles() {
        log.info("Initializing default roles");
        
        for (UserRole userRole : UserRole.values()) {
            if (!roleRepository.existsByName(userRole)) {
                Role role = new Role(userRole, userRole.getDescription());
                roleRepository.save(role);
                log.info("Created default role: {}", userRole);
            }
        }
    }
}

