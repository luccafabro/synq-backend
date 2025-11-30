package com.synq.backend.service.impl;

import com.synq.backend.dto.response.MembershipDto;
import com.synq.backend.enums.MembershipRole;
import com.synq.backend.exceptions.EndpointException;
import com.synq.backend.mapper.MembershipMapper;
import com.synq.backend.model.Frequency;
import com.synq.backend.model.Membership;
import com.synq.backend.model.User;
import com.synq.backend.repository.FrequencyRepository;
import com.synq.backend.repository.MembershipRepository;
import com.synq.backend.repository.UserRepository;
import com.synq.backend.service.MembershipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of MembershipService
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MembershipServiceImpl implements MembershipService {

    private final MembershipRepository membershipRepository;
    private final UserRepository userRepository;
    private final FrequencyRepository frequencyRepository;
    private final MembershipMapper membershipMapper;

    @Override
    public MembershipDto joinFrequency(Long userId, Long frequencyId, MembershipRole role, String nickname) {
        log.debug("User {} joining frequency {}", userId, frequencyId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EndpointException("User not found", HttpStatus.NOT_FOUND));

        Frequency frequency = frequencyRepository.findById(frequencyId)
                .orElseThrow(() -> new EndpointException("Frequency not found", HttpStatus.NOT_FOUND));

        if (membershipRepository.existsByUserIdAndFrequencyId(userId, frequencyId)) {
            throw new EndpointException("User is already a member", HttpStatus.CONFLICT);
        }

        long memberCount = membershipRepository.countByFrequencyId(frequencyId);
        if (memberCount >= frequency.getMaxParticipants()) {
            throw new EndpointException("Frequency has reached maximum participants", HttpStatus.BAD_REQUEST);
        }

        Membership membership = Membership.builder()
                .user(user)
                .frequency(frequency)
                .role(role != null ? role : MembershipRole.MEMBER)
                .joinedAt(LocalDateTime.now())
                .nickname(nickname)
                .isBanned(false)
                .build();

        Membership savedMembership = membershipRepository.save(membership);
        log.info("User {} joined frequency {} successfully", userId, frequencyId);

        return membershipMapper.toDto(savedMembership);
    }

    @Override
    public void leaveFrequency(Long userId, Long frequencyId) {
        log.debug("User {} leaving frequency {}", userId, frequencyId);

        Membership membership = membershipRepository.findByUserIdAndFrequencyId(userId, frequencyId)
                .orElseThrow(() -> new EndpointException("Membership not found", HttpStatus.NOT_FOUND));

        if (membership.getRole() == MembershipRole.OWNER) {
            long ownerCount = membershipRepository.findByFrequencyIdAndRole(frequencyId, MembershipRole.OWNER).size();
            if (ownerCount <= 1) {
                throw new EndpointException("Cannot leave: frequency must have at least one owner", HttpStatus.BAD_REQUEST);
            }
        }

        membershipRepository.deleteByUserIdAndFrequencyId(userId, frequencyId);
        log.info("User {} left frequency {} successfully", userId, frequencyId);
    }

    @Override
    public MembershipDto updateMembershipRole(Long userId, Long frequencyId, MembershipRole newRole) {
        log.debug("Updating role for user {} in frequency {}", userId, frequencyId);

        Membership membership = membershipRepository.findByUserIdAndFrequencyId(userId, frequencyId)
                .orElseThrow(() -> new EndpointException("Membership not found", HttpStatus.NOT_FOUND));

        membership.setRole(newRole);
        Membership updatedMembership = membershipRepository.save(membership);

        log.info("Role updated successfully for user {} in frequency {}", userId, frequencyId);
        return membershipMapper.toDto(updatedMembership);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MembershipDto> getMembershipByUserAndFrequency(Long userId, Long frequencyId) {
        return membershipRepository.findByUserIdAndFrequencyId(userId, frequencyId)
                .map(membershipMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Membership> getMembershipEntityByUserAndFrequency(Long userId, Long frequencyId) {
        return membershipRepository.findByUserIdAndFrequencyId(userId, frequencyId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MembershipDto> getMembershipsByUser(Long userId) {
        log.debug("Fetching memberships for user {}", userId);
        return membershipRepository.findByUserId(userId).stream()
                .map(membershipMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MembershipDto> getMembershipsByFrequency(Long frequencyId) {
        log.debug("Fetching memberships for frequency {}", frequencyId);
        return membershipRepository.findByFrequencyId(frequencyId).stream()
                .map(membershipMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isMember(Long userId, Long frequencyId) {
        return membershipRepository.existsByUserIdAndFrequencyId(userId, frequencyId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasRole(Long userId, Long frequencyId, MembershipRole role) {
        return membershipRepository.findByUserIdAndFrequencyId(userId, frequencyId)
                .map(m -> m.getRole() == role)
                .orElse(false);
    }

    @Override
    public MembershipDto toggleBan(Long userId, Long frequencyId, boolean banned) {
        log.debug("Toggling ban for user {} in frequency {}", userId, frequencyId);

        Membership membership = membershipRepository.findByUserIdAndFrequencyId(userId, frequencyId)
                .orElseThrow(() -> new EndpointException("Membership not found", HttpStatus.NOT_FOUND));

        membership.setBanned(banned);
        Membership updatedMembership = membershipRepository.save(membership);

        log.info("Ban status updated for user {} in frequency {}", userId, frequencyId);
        return membershipMapper.toDto(updatedMembership);
    }
}

