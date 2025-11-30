package com.synq.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Map;

/**
 * DTO for creating a new frequency
 */
public record CreateFrequencyDto(
        @NotBlank(message = "Name is required")
        @Size(min = 3, max = 200, message = "Name must be between 3 and 200 characters")
        String name,

        @NotBlank(message = "Slug is required")
        @Size(min = 3, max = 255, message = "Slug must be between 3 and 255 characters")
        String slug,

        String description,
        boolean isPrivate,
        Map<String, Object> settings,
        String coverImageUrl,
        Integer maxParticipants
) {
}

