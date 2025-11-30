package com.synq.backend.service;

import com.synq.backend.AbstractIntegrationTest;
import com.synq.backend.dto.request.CreateMessageDto;
import com.synq.backend.dto.response.MessageDto;
import com.synq.backend.enums.MembershipRole;
import com.synq.backend.enums.MessageType;
import com.synq.backend.model.Frequency;
import com.synq.backend.model.Membership;
import com.synq.backend.model.User;
import com.synq.backend.repository.FrequencyRepository;
import com.synq.backend.repository.MembershipRepository;
import com.synq.backend.repository.MessageRepository;
import com.synq.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for MessageService - tests message posting and persistence
 */
@Transactional
class MessageServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FrequencyRepository frequencyRepository;

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private MessageRepository messageRepository;

    private User testUser;
    private Frequency testFrequency;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = User.builder()
                .username("messagetest")
                .email("message@test.com")
                .displayName("Message Tester")
                .build();
        testUser = userRepository.save(testUser);

        // Create test frequency
        testFrequency = Frequency.builder()
                .name("Test Frequency")
                .slug("test-frequency")
                .description("Test Description")
                .isPrivate(false)
                .owner(testUser)
                .maxParticipants(100)
                .build();
        testFrequency = frequencyRepository.save(testFrequency);

        // Create membership
        Membership membership = Membership.builder()
                .user(testUser)
                .frequency(testFrequency)
                .role(MembershipRole.OWNER)
                .joinedAt(LocalDateTime.now())
                .build();
        membershipRepository.save(membership);
    }

    @Test
    @DisplayName("Should post message linked to frequency and author")
    void testPostMessage() {
        // Given
        CreateMessageDto dto = new CreateMessageDto(
                testFrequency.getId(),
                "Hello, this is a test message!",
                MessageType.TEXT,
                null,
                new HashMap<>()
        );

        // When
        MessageDto postedMessage = messageService.postMessage(dto, testUser.getId());

        // Then
        assertThat(postedMessage).isNotNull();
        assertThat(postedMessage.id()).isNotNull();
        assertThat(postedMessage.content()).isEqualTo("Hello, this is a test message!");
        assertThat(postedMessage.frequencyId()).isEqualTo(testFrequency.getId());
        assertThat(postedMessage.authorId()).isEqualTo(testUser.getId());
        assertThat(postedMessage.authorUsername()).isEqualTo(testUser.getUsername());
        assertThat(postedMessage.type()).isEqualTo(MessageType.TEXT);
        assertThat(postedMessage.createdAt()).isNotNull();

        // Verify persistence
        assertThat(messageRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should post message with reply reference")
    void testPostMessageWithReply() {
        // Given - post original message
        CreateMessageDto originalDto = new CreateMessageDto(
                testFrequency.getId(),
                "Original message",
                MessageType.TEXT,
                null,
                null
        );
        MessageDto originalMessage = messageService.postMessage(originalDto, testUser.getId());

        // When - post reply
        CreateMessageDto replyDto = new CreateMessageDto(
                testFrequency.getId(),
                "This is a reply",
                MessageType.TEXT,
                originalMessage.id(),
                null
        );
        MessageDto replyMessage = messageService.postMessage(replyDto, testUser.getId());

        // Then
        assertThat(replyMessage).isNotNull();
        assertThat(replyMessage.replyToId()).isEqualTo(originalMessage.id());
        assertThat(replyMessage.content()).isEqualTo("This is a reply");
    }

    @Test
    @DisplayName("Should retrieve messages by frequency")
    void testGetMessagesByFrequency() {
        // Given - post multiple messages
        for (int i = 1; i <= 3; i++) {
            CreateMessageDto dto = new CreateMessageDto(
                    testFrequency.getId(),
                    "Message " + i,
                    MessageType.TEXT,
                    null,
                    null
            );
            messageService.postMessage(dto, testUser.getId());
        }

        // When
        var messages = messageService.getMessagesByFrequencyCursor(
                testFrequency.getId(),
                LocalDateTime.now().plusSeconds(1)
        );

        // Then
        assertThat(messages).hasSize(3);
        assertThat(messages).extracting(MessageDto::content)
                .containsExactly("Message 3", "Message 2", "Message 1");
    }
}

