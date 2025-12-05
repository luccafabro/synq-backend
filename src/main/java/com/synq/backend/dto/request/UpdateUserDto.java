package com.synq.backend.dto.request;

import com.synq.backend.enums.UserRole;
import com.synq.backend.enums.UserStatus;

import java.util.Map;
import java.util.Set;

/**
 * DTO for updating an existing user
 */
public record UpdateUserDto(
        String firstName,
        String lastName,
        String avatarUrl,
        String email,
        Boolean emailVerified,
        UserStatus status,
        Map<String, Object> metadata,
        Set<UserRole> roles
) {
}

