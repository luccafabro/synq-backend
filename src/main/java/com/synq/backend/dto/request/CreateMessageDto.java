package com.synq.backend.dto.request;

import com.synq.backend.enums.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

/**
 * DTO for creating a new message
 */
public record CreateMessageDto(
        @NotNull(message = "Frequency ID is required")
        Long frequencyId,

        @NotBlank(message = "Content is required")
        String content,

        MessageType type,
        Long replyToId,
        Map<String, Object> metadata
) {
}

