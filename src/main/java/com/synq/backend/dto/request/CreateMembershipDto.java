package com.synq.backend.dto.request;

import com.synq.backend.enums.MembershipRole;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for creating a new membership
 */
public record CreateMembershipDto(
        @NotNull(message = "User ID is required")
        Long userId,

        @NotNull(message = "Frequency ID is required")
        Long frequencyId,

        MembershipRole role,
        String nickname
) {
}

