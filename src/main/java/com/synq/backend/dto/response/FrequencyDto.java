package com.synq.backend.dto.response;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO for frequency response
 */
public record FrequencyDto(
        Long id,
        String externalId,
        String name,
        String slug,
        String description,
        boolean isPrivate,
        Long ownerId,
        Map<String, Object> settings,
        String coverImageUrl,
        int maxParticipants,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

