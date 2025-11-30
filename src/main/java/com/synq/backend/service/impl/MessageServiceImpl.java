package com.synq.backend.service.impl;

import com.synq.backend.dto.request.CreateMessageDto;
import com.synq.backend.dto.request.UpdateMessageDto;
import com.synq.backend.dto.response.MessageDto;
import com.synq.backend.enums.MessageType;
import com.synq.backend.exceptions.EndpointException;
import com.synq.backend.mapper.MessageMapper;
import com.synq.backend.model.Frequency;
import com.synq.backend.model.Message;
import com.synq.backend.model.User;
import com.synq.backend.repository.FrequencyRepository;
import com.synq.backend.repository.MembershipRepository;
import com.synq.backend.repository.MessageRepository;
import com.synq.backend.repository.UserRepository;
import com.synq.backend.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of MessageService
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final FrequencyRepository frequencyRepository;
    private final MembershipRepository membershipRepository;
    private final MessageMapper messageMapper;

    @Override
    public MessageDto postMessage(CreateMessageDto dto, Long authorId) {
        log.debug("Posting message in frequency {} by user {}", dto.frequencyId(), authorId);

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new EndpointException("Author not found", HttpStatus.NOT_FOUND));

        Frequency frequency = frequencyRepository.findById(dto.frequencyId())
                .orElseThrow(() -> new EndpointException("Frequency not found", HttpStatus.NOT_FOUND));

        // Check if user is a member
        if (!membershipRepository.existsByUserIdAndFrequencyId(authorId, dto.frequencyId())) {
            throw new EndpointException("User is not a member of this frequency", HttpStatus.FORBIDDEN);
        }

        Message message = messageMapper.toEntity(dto);
        message.setAuthor(author);
        message.setFrequency(frequency);

        if (message.getType() == null) {
            message.setType(MessageType.TEXT);
        }

        // Handle reply
        if (dto.replyToId() != null) {
            Message replyToMessage = messageRepository.findById(dto.replyToId())
                    .orElseThrow(() -> new EndpointException("Reply-to message not found", HttpStatus.NOT_FOUND));
            message.setReplyTo(replyToMessage);
        }

        Message savedMessage = messageRepository.save(message);
        log.info("Message posted successfully with ID: {}", savedMessage.getId());

        return messageMapper.toDto(savedMessage);
    }

    @Override
    public MessageDto updateMessage(Long id, UpdateMessageDto dto, Long authorId) {
        log.debug("Updating message {} by user {}", id, authorId);

        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new EndpointException("Message not found", HttpStatus.NOT_FOUND));

        if (!message.getAuthor().getId().equals(authorId)) {
            throw new EndpointException("Only the author can edit this message", HttpStatus.FORBIDDEN);
        }

        if (message.getDeletedAt() != null) {
            throw new EndpointException("Cannot edit deleted message", HttpStatus.BAD_REQUEST);
        }

        messageMapper.updateEntity(dto, message);
        message.setEditedAt(LocalDateTime.now());

        Message updatedMessage = messageRepository.save(message);
        log.info("Message updated successfully with ID: {}", id);

        return messageMapper.toDto(updatedMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MessageDto> getMessageById(Long id) {
        log.debug("Fetching message by ID: {}", id);
        return messageRepository.findById(id)
                .map(messageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageDto> getMessagesByFrequency(Long frequencyId, Pageable pageable) {
        log.debug("Fetching messages for frequency {}", frequencyId);
        return messageRepository.findActiveMessagesByFrequencyId(frequencyId, pageable).stream()
                .map(messageMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageDto> getMessagesByFrequencyCursor(Long frequencyId, LocalDateTime before) {
        log.debug("Fetching messages for frequency {} before {}", frequencyId, before);
        LocalDateTime cursor = before != null ? before : LocalDateTime.now();

        return messageRepository
                .findTop50ByFrequencyIdAndCreatedAtBeforeOrderByCreatedAtDesc(frequencyId, cursor)
                .stream()
                .map(messageMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageDto> getReplies(Long messageId) {
        log.debug("Fetching replies for message {}", messageId);
        return messageRepository.findByReplyToIdOrderByCreatedAtAsc(messageId).stream()
                .filter(msg -> msg.getDeletedAt() == null)
                .map(messageMapper::toDto)
                .toList();
    }

    @Override
    public void deleteMessage(Long id, Long authorId) {
        log.debug("Deleting message {} by user {}", id, authorId);

        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new EndpointException("Message not found", HttpStatus.NOT_FOUND));

        if (!message.getAuthor().getId().equals(authorId)) {
            throw new EndpointException("Only the author can delete this message", HttpStatus.FORBIDDEN);
        }

        message.setDeletedAt(LocalDateTime.now());
        messageRepository.save(message);

        log.info("Message soft-deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canAccessMessage(Long userId, Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new EndpointException("Message not found", HttpStatus.NOT_FOUND));

        Frequency frequency = message.getFrequency();

        if (!frequency.isPrivate()) {
            return true;
        }

        return membershipRepository.existsByUserIdAndFrequencyId(userId, frequency.getId());
    }
}

