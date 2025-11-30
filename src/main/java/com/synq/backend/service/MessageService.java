package com.synq.backend.service;

import com.synq.backend.dto.request.CreateMessageDto;
import com.synq.backend.dto.request.UpdateMessageDto;
import com.synq.backend.dto.response.MessageDto;
import com.synq.backend.model.Message;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for Message operations
 */
public interface MessageService {

    /**
     * Post a new message
     */
    MessageDto postMessage(CreateMessageDto dto, Long authorId);

    /**
     * Update an existing message
     */
    MessageDto updateMessage(Long id, UpdateMessageDto dto, Long authorId);

    /**
     * Get message by ID
     */
    Optional<MessageDto> getMessageById(Long id);

    /**
     * Get messages by frequency with pagination
     */
    List<MessageDto> getMessagesByFrequency(Long frequencyId, Pageable pageable);

    /**
     * Get messages by frequency with cursor-based pagination
     */
    List<MessageDto> getMessagesByFrequencyCursor(Long frequencyId, LocalDateTime before);

    /**
     * Get replies to a message
     */
    List<MessageDto> getReplies(Long messageId);

    /**
     * Soft delete a message
     */
    void deleteMessage(Long id, Long authorId);

    /**
     * Check if user can access message
     */
    boolean canAccessMessage(Long userId, Long messageId);
}

