package com.synq.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * AuditLog entity for tracking user actions and system events.
 * This entity does not extend BaseEntity as it has a simplified structure.
 */
@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_actor_id", columnList = "actorId"),
        @Index(name = "idx_action_type", columnList = "actionType"),
        @Index(name = "idx_created_at", columnList = "createdAt")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 36, nullable = false)
    private String actorId;

    @Column(nullable = false, length = 100)
    private String actionType;

    @Column(nullable = false, length = 100)
    private String targetType;

    @Column(length = 36)
    private String targetId;

    /**
     * Arbitrary data stored as JSON (before/after state, additional context)
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "JSON")
    private Map<String, Object> data;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}

