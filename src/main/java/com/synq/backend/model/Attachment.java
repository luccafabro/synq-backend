package com.synq.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.synq.backend.core.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Attachment entity for files attached to messages.
 */
@Entity
@Table(name = "attachments", indexes = {
        @Index(name = "idx_message_id", columnList = "message_id"),
        @Index(name = "idx_uploader_id", columnList = "uploader_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Attachment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    @JsonIgnore
    private Message message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_id", nullable = false)
    @JsonIgnore
    private User uploader;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String fileUrl;

    @Column(length = 100)
    private String mimeType;

    @Column(nullable = false)
    private Long sizeBytes;

    @Column(length = 500)
    private String storageKey;

    @Column(columnDefinition = "TEXT")
    private String thumbnailUrl;
}

