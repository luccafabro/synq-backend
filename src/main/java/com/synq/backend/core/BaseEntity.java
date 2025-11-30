package com.synq.backend.core;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    @Column(nullable = false, columnDefinition = "tinyint default 1")
    private boolean active;

    @Column(length = 36, nullable = false)
    private String externalId;

    @PrePersist
    public void prePersist() {
        this.setExternalId(UUID.randomUUID().toString());
        this.setActive(true);
        this.onPrePersist();
    }
    protected void onPrePersist() {}

    @PreUpdate
    public void preUpdate() {
        this.setUpdatedAt(LocalDateTime.now());
        this.onPreUpdate();
    }
    protected void onPreUpdate() {}


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity entity = (BaseEntity) o;
        return externalId != null && externalId.equals(entity.externalId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(externalId);
    }
}
