package com.synq.backend.service;

import com.synq.backend.dto.response.MembershipDto;
import com.synq.backend.enums.MembershipRole;
import com.synq.backend.model.Membership;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for Membership operations
 */
public interface MembershipService {

    /**
     * Join a frequency
     */
    MembershipDto joinFrequency(Long userId, Long frequencyId, MembershipRole role, String nickname);

    /**
     * Leave a frequency
     */
    void leaveFrequency(Long userId, Long frequencyId);

    /**
     * Update membership role
     */
    MembershipDto updateMembershipRole(Long userId, Long frequencyId, MembershipRole newRole);

    /**
     * Get membership by user and frequency
     */
    Optional<MembershipDto> getMembershipByUserAndFrequency(Long userId, Long frequencyId);

    /**
     * Get membership entity (for internal use)
     */
    Optional<Membership> getMembershipEntityByUserAndFrequency(Long userId, Long frequencyId);

    /**
     * Get all memberships for a user
     */
    List<MembershipDto> getMembershipsByUser(Long userId);

    /**
     * Get all memberships for a frequency
     */
    List<MembershipDto> getMembershipsByFrequency(Long frequencyId);

    /**
     * Check if user is member of frequency
     */
    boolean isMember(Long userId, Long frequencyId);

    /**
     * Check if user has role in frequency
     */
    boolean hasRole(Long userId, Long frequencyId, MembershipRole role);

    /**
     * Ban/unban member
     */
    MembershipDto toggleBan(Long userId, Long frequencyId, boolean banned);
}

