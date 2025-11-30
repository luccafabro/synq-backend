package com.synq.backend.dto.request;

import java.util.Map;

/**
 * DTO for updating an existing frequency
 */
public record UpdateFrequencyDto(
        String name,
        String description,
        Boolean isPrivate,
        Map<String, Object> settings,
        String coverImageUrl,
        Integer maxParticipants
) {
}

