package com.synq.backend.dto.request;

import java.util.Map;

/**
 * DTO for updating an existing message
 */
public record UpdateMessageDto(
        String content,
        Map<String, Object> metadata
) {
}

