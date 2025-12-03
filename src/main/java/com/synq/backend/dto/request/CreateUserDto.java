package com.synq.backend.dto.request;

import com.synq.backend.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Map;

/**
 * DTO for creating a new user
 */
public record CreateUserDto(
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
        String username,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        String firstName,
        String lastName,
        String avatarUrl,
        boolean emailVerified,
        UserStatus status,
        String keycloakExternalId,
        Map<String, Object> metadata
) {
}

