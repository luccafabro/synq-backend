package com.synq.backend.repository;

import com.synq.backend.enums.MembershipRole;
import com.synq.backend.model.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Membership entity.
 */
@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {

    /**
     * Find membership by user and frequency
     */
    Optional<Membership> findByUserIdAndFrequencyId(Long userId, Long frequencyId);

    /**
     * Find all memberships for a user
     */
    List<Membership> findByUserId(Long userId);

    /**
     * Find all memberships for a frequency
     */
    List<Membership> findByFrequencyId(Long frequencyId);

    /**
     * Find memberships by frequency and role
     */
    List<Membership> findByFrequencyIdAndRole(Long frequencyId, MembershipRole role);

    /**
     * Count members in a frequency
     */
    long countByFrequencyId(Long frequencyId);

    /**
     * Check if user is a member of frequency
     */
    boolean existsByUserIdAndFrequencyId(Long userId, Long frequencyId);

    /**
     * Delete membership by user and frequency
     */
    void deleteByUserIdAndFrequencyId(Long userId, Long frequencyId);
}

