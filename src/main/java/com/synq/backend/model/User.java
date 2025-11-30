package com.synq.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.synq.backend.core.BaseEntity;
import com.synq.backend.enums.UserStatus;
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
 * User entity representing authenticated users.
 * externalId is populated from Keycloak sub claim for SSO integration.
 */
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_username", columnList = "username", unique = true),
        @Index(name = "idx_email", columnList = "email", unique = true),
        @Index(name = "idx_keycloak_external_id", columnList = "keycloakExternalId")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class User extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(length = 150)
    private String displayName;

    @Column(length = 500)
    private String avatarUrl;

    @Column(nullable = false, columnDefinition = "tinyint default 0")
    private boolean emailVerified;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    /**
     * Keycloak sub claim - unique identifier from Keycloak SSO
     */
    @Column(name = "keycloak_external_id", length = 255)
    private String keycloakExternalId;

    /**
     * Arbitrary metadata stored as JSON (preferences, settings, etc.)
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "JSON")
    private Map<String, Object> metadata;

    @Column
    private LocalDateTime lastLoginAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    private List<Membership> memberships = new ArrayList<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    private List<Message> messages = new ArrayList<>();
}

