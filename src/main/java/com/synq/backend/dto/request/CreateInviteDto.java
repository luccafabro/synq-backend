package com.synq.backend.dto.request;

import com.synq.backend.enums.MembershipRole;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * DTO for creating a new invite
 */
public record CreateInviteDto(
        @NotNull(message = "Frequency ID is required")
        Long frequencyId,

        LocalDateTime expiresAt,
        Integer maxUses,
        MembershipRole roleOnJoin
) {
}

