package com.synq.backend.dto.response;

import com.synq.backend.enums.MembershipRole;

import java.time.LocalDateTime;

/**
 * DTO for invite response
 */
public record InviteDto(
        Long id,
        String externalId,
        Long frequencyId,
        String frequencyName,
        Long inviterId,
        String inviterUsername,
        String token,
        LocalDateTime expiresAt,
        int maxUses,
        int usesCount,
        MembershipRole roleOnJoin,
        LocalDateTime createdAt
) {
}

