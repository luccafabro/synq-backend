package com.synq.backend.dto.response;

import com.synq.backend.enums.UserRole;

/**
 * DTO for role response
 */
public record RoleDto(
        Long id,
        String externalId,
        UserRole name,
        String description
) {
}

