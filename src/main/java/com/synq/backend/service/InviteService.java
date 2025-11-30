package com.synq.backend.service;

import com.synq.backend.dto.request.CreateInviteDto;
import com.synq.backend.dto.response.InviteDto;
import com.synq.backend.dto.response.MembershipDto;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for Invite operations
 */
public interface InviteService {

    /**
     * Create a new invite
     */
    InviteDto createInvite(CreateInviteDto dto, Long inviterId);

    /**
     * Redeem an invite (join frequency via invite token)
     */
    MembershipDto redeemInvite(String token, Long userId);

    /**
     * Get invite by token
     */
    Optional<InviteDto> getInviteByToken(String token);

    /**
     * Get all invites for a frequency
     */
    List<InviteDto> getInvitesByFrequency(Long frequencyId);

    /**
     * Delete/revoke an invite
     */
    void deleteInvite(Long id);

    /**
     * Check if invite is valid (not expired, not maxed out)
     */
    boolean isInviteValid(String token);
}

