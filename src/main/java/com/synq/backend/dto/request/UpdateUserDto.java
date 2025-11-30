package com.synq.backend.dto.request;

import com.synq.backend.enums.UserStatus;

import java.util.Map;

/**
 * DTO for updating an existing user
 */
public record UpdateUserDto(
        String displayName,
        String avatarUrl,
        Boolean emailVerified,
        UserStatus status,
        Map<String, Object> metadata
) {
}

