package com.synq.backend.dto.response;

import com.synq.backend.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO for user response
 */
public record UserDto(
        Long id,
        String externalId,
        String username,
        String email,
        String displayName,
        String avatarUrl,
        boolean emailVerified,
        UserStatus status,
        String keycloakExternalId,
        Map<String, Object> metadata,
        LocalDateTime lastLoginAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

