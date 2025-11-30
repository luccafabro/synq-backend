package com.synq.backend.dto.response;

import com.synq.backend.enums.MembershipRole;

import java.time.LocalDateTime;

/**
 * DTO for membership response
 */
public record MembershipDto(
        Long id,
        Long userId,
        String username,
        Long frequencyId,
        String frequencyName,
        MembershipRole role,
        LocalDateTime joinedAt,
        LocalDateTime mutedUntil,
        boolean isBanned,
        String nickname
) {
}

