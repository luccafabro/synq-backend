package com.synq.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.synq.backend.core.BaseEntity;
import com.synq.backend.enums.MembershipRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * Invite entity for inviting users to frequencies.
 */
@Entity
@Table(name = "invites", indexes = {
        @Index(name = "idx_frequency_id", columnList = "frequency_id"),
        @Index(name = "idx_inviter_id", columnList = "inviter_id"),
        @Index(name = "idx_token", columnList = "token", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Invite extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "frequency_id", nullable = false)
    @JsonIgnore
    private Frequency frequency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inviter_id", nullable = false)
    @JsonIgnore
    private User inviter;

    @Column(nullable = false, unique = true, length = 100)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    @Builder.Default
    private int maxUses = 1;

    @Column(nullable = false)
    @Builder.Default
    private int usesCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private MembershipRole roleOnJoin = MembershipRole.MEMBER;
}

