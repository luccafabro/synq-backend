package com.synq.backend.dto.response;

import com.synq.backend.enums.MessageType;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO for message response
 */
public record MessageDto(
        Long id,
        String externalId,
        Long frequencyId,
        Long authorId,
        String authorUsername,
        String content,
        MessageType type,
        Long replyToId,
        LocalDateTime createdAt,
        LocalDateTime editedAt,
        LocalDateTime deletedAt,
        Map<String, Object> metadata
) {
}

