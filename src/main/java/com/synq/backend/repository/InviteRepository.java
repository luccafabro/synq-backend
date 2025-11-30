package com.synq.backend.repository;

import com.synq.backend.model.Invite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Invite entity.
 */
@Repository
public interface InviteRepository extends JpaRepository<Invite, Long> {

    /**
     * Find invite by token
     */
    Optional<Invite> findByToken(String token);

    /**
     * Find invites by frequency
     */
    List<Invite> findByFrequencyId(Long frequencyId);

    /**
     * Find active invites by frequency (not expired, not maxed out)
     */
    List<Invite> findByFrequencyIdAndExpiresAtAfterAndUsesCountLessThan(
            Long frequencyId,
            LocalDateTime now,
            int maxUses
    );

    /**
     * Find invites by inviter
     */
    List<Invite> findByInviterId(Long inviterId);

    /**
     * Check if token exists
     */
    boolean existsByToken(String token);
}

