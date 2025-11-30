package com.synq.backend.service.impl;

import com.synq.backend.dto.request.CreateInviteDto;
import com.synq.backend.dto.response.InviteDto;
import com.synq.backend.dto.response.MembershipDto;
import com.synq.backend.enums.MembershipRole;
import com.synq.backend.exceptions.EndpointException;
import com.synq.backend.mapper.InviteMapper;
import com.synq.backend.model.Frequency;
import com.synq.backend.model.Invite;
import com.synq.backend.model.User;
import com.synq.backend.repository.FrequencyRepository;
import com.synq.backend.repository.InviteRepository;
import com.synq.backend.repository.UserRepository;
import com.synq.backend.service.InviteService;
import com.synq.backend.service.MembershipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of InviteService
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InviteServiceImpl implements InviteService {

    private final InviteRepository inviteRepository;
    private final UserRepository userRepository;
    private final FrequencyRepository frequencyRepository;
    private final MembershipService membershipService;
    private final InviteMapper inviteMapper;

    @Override
    public InviteDto createInvite(CreateInviteDto dto, Long inviterId) {
        log.debug("Creating invite for frequency {} by user {}", dto.frequencyId(), inviterId);

        User inviter = userRepository.findById(inviterId)
                .orElseThrow(() -> new EndpointException("Inviter not found", HttpStatus.NOT_FOUND));

        Frequency frequency = frequencyRepository.findById(dto.frequencyId())
                .orElseThrow(() -> new EndpointException("Frequency not found", HttpStatus.NOT_FOUND));

        // Check if inviter has permission (must be member)
        if (!membershipService.isMember(inviterId, dto.frequencyId())) {
            throw new EndpointException("Only members can create invites", HttpStatus.FORBIDDEN);
        }

        String token = generateUniqueToken();
        LocalDateTime expiresAt = dto.expiresAt() != null ? dto.expiresAt() : LocalDateTime.now().plusDays(7);
        int maxUses = dto.maxUses() != null ? dto.maxUses() : 1;
        MembershipRole roleOnJoin = dto.roleOnJoin() != null ? dto.roleOnJoin() : MembershipRole.MEMBER;

        Invite invite = Invite.builder()
                .frequency(frequency)
                .inviter(inviter)
                .token(token)
                .expiresAt(expiresAt)
                .maxUses(maxUses)
                .usesCount(0)
                .roleOnJoin(roleOnJoin)
                .build();

        Invite savedInvite = inviteRepository.save(invite);
        log.info("Invite created successfully with token: {}", token);

        return inviteMapper.toDto(savedInvite);
    }

    @Override
    public MembershipDto redeemInvite(String token, Long userId) {
        log.debug("Redeeming invite {} by user {}", token, userId);

        Invite invite = inviteRepository.findByToken(token)
                .orElseThrow(() -> new EndpointException("Invite not found", HttpStatus.NOT_FOUND));

        // Validate invite
        if (!isInviteValid(token)) {
            throw new EndpointException("Invite is expired or maxed out", HttpStatus.BAD_REQUEST);
        }

        // Check if user is already a member
        if (membershipService.isMember(userId, invite.getFrequency().getId())) {
            throw new EndpointException("User is already a member", HttpStatus.CONFLICT);
        }

        // Join frequency
        MembershipDto membership = membershipService.joinFrequency(
                userId,
                invite.getFrequency().getId(),
                invite.getRoleOnJoin(),
                null
        );

        // Increment uses count
        invite.setUsesCount(invite.getUsesCount() + 1);
        inviteRepository.save(invite);

        log.info("Invite redeemed successfully by user {}", userId);
        return membership;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InviteDto> getInviteByToken(String token) {
        log.debug("Fetching invite by token: {}", token);
        return inviteRepository.findByToken(token)
                .map(inviteMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InviteDto> getInvitesByFrequency(Long frequencyId) {
        log.debug("Fetching invites for frequency {}", frequencyId);
        return inviteRepository.findByFrequencyId(frequencyId).stream()
                .map(inviteMapper::toDto)
                .toList();
    }

    @Override
    public void deleteInvite(Long id) {
        log.debug("Deleting invite with ID: {}", id);

        if (!inviteRepository.existsById(id)) {
            throw new EndpointException("Invite not found", HttpStatus.NOT_FOUND);
        }

        inviteRepository.deleteById(id);
        log.info("Invite deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isInviteValid(String token) {
        return inviteRepository.findByToken(token)
                .map(invite -> {
                    LocalDateTime now = LocalDateTime.now();
                    boolean notExpired = invite.getExpiresAt().isAfter(now);
                    boolean notMaxedOut = invite.getUsesCount() < invite.getMaxUses();
                    return notExpired && notMaxedOut;
                })
                .orElse(false);
    }

    private String generateUniqueToken() {
        String token;
        do {
            token = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        } while (inviteRepository.existsByToken(token));
        return token;
    }
}

