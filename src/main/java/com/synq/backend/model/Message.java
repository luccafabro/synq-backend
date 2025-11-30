package com.synq.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.synq.backend.core.BaseEntity;
import com.synq.backend.enums.MessageType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Message entity representing a message posted in a frequency.
 * Supports reply threading via replyTo self-reference.
 */
@Entity
@Table(name = "messages", indexes = {
        @Index(name = "idx_frequency_created", columnList = "frequency_id, createdAt"),
        @Index(name = "idx_author_id", columnList = "author_id"),
        @Index(name = "idx_reply_to_id", columnList = "reply_to_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Message extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "frequency_id", nullable = false)
    @JsonIgnore
    private Frequency frequency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    @JsonIgnore
    private User author;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private MessageType type = MessageType.TEXT;

    /**
     * Self-referencing for reply threads
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_to_id")
    @JsonIgnore
    private Message replyTo;

    @Column
    private LocalDateTime editedAt;

    @Column
    private LocalDateTime deletedAt;

    /**
     * Arbitrary metadata stored as JSON (mentions, reactions, etc.)
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "JSON")
    private Map<String, Object> metadata;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    private List<Attachment> attachments = new ArrayList<>();

    @OneToMany(mappedBy = "replyTo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    private List<Message> replies = new ArrayList<>();
}

