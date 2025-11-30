package com.synq.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.synq.backend.core.BaseEntity;
import com.synq.backend.enums.MembershipRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * Membership entity representing a user's membership in a frequency.
 * Enforces unique constraint on (user_id, frequency_id).
 */
@Entity
@Table(name = "memberships",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_frequency", columnNames = {"user_id", "frequency_id"})
    },
    indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_frequency_id", columnList = "frequency_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Membership extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "frequency_id", nullable = false)
    @JsonIgnore
    private Frequency frequency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private MembershipRole role = MembershipRole.MEMBER;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime joinedAt = LocalDateTime.now();

    @Column
    private LocalDateTime mutedUntil;

    @Column(nullable = false, columnDefinition = "tinyint default 0")
    private boolean isBanned;

    @Column(length = 100)
    private String nickname;
}

