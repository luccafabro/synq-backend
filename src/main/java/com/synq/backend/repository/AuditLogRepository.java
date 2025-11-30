package com.synq.backend.repository;

import com.synq.backend.model.AuditLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for AuditLog entity.
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /**
     * Find audit logs by actor
     */
    List<AuditLog> findByActorIdOrderByCreatedAtDesc(String actorId, Pageable pageable);

    /**
     * Find audit logs by action type
     */
    List<AuditLog> findByActionTypeOrderByCreatedAtDesc(String actionType, Pageable pageable);

    /**
     * Find audit logs by target
     */
    List<AuditLog> findByTargetTypeAndTargetIdOrderByCreatedAtDesc(
            String targetType,
            String targetId,
            Pageable pageable
    );

    /**
     * Find audit logs within date range
     */
    List<AuditLog> findByCreatedAtBetweenOrderByCreatedAtDesc(
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );
}

