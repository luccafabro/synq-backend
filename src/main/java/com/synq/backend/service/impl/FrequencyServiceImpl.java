package com.synq.backend.service.impl;

import com.synq.backend.dto.request.CreateFrequencyDto;
import com.synq.backend.dto.request.UpdateFrequencyDto;
import com.synq.backend.dto.response.FrequencyDto;
import com.synq.backend.enums.MembershipRole;
import com.synq.backend.exceptions.EndpointException;
import com.synq.backend.mapper.FrequencyMapper;
import com.synq.backend.model.Frequency;
import com.synq.backend.model.Membership;
import com.synq.backend.model.User;
import com.synq.backend.repository.FrequencyRepository;
import com.synq.backend.repository.MembershipRepository;
import com.synq.backend.repository.UserRepository;
import com.synq.backend.service.FrequencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of FrequencyService using SOLID principles
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FrequencyServiceImpl implements FrequencyService {

    private final FrequencyRepository frequencyRepository;
    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final FrequencyMapper frequencyMapper;

    @Override
    public FrequencyDto createFrequency(CreateFrequencyDto dto, Long ownerId) {
        log.debug("Creating frequency with slug: {}", dto.slug());

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new EndpointException("Owner not found", HttpStatus.NOT_FOUND));

        if (frequencyRepository.existsBySlug(dto.slug())) {
            throw new EndpointException("Slug already exists", HttpStatus.CONFLICT);
        }

        Frequency frequency = frequencyMapper.toEntity(dto);
        frequency.setOwner(owner);

        if (frequency.getMaxParticipants() == 0) {
            frequency.setMaxParticipants(1000);
        }

        Frequency savedFrequency = frequencyRepository.save(frequency);

        // Create owner membership
        Membership ownerMembership = Membership.builder()
                .user(owner)
                .frequency(savedFrequency)
                .role(MembershipRole.OWNER)
                .joinedAt(LocalDateTime.now())
                .isBanned(false)
                .build();
        membershipRepository.save(ownerMembership);

        log.info("Frequency created successfully with ID: {}", savedFrequency.getId());
        return frequencyMapper.toDto(savedFrequency);
    }

    @Override
    public FrequencyDto updateFrequency(Long id, UpdateFrequencyDto dto) {
        log.debug("Updating frequency with ID: {}", id);

        Frequency frequency = frequencyRepository.findById(id)
                .orElseThrow(() -> new EndpointException("Frequency not found", HttpStatus.NOT_FOUND));

        frequencyMapper.updateEntity(dto, frequency);
        Frequency updatedFrequency = frequencyRepository.save(frequency);

        log.info("Frequency updated successfully with ID: {}", id);
        return frequencyMapper.toDto(updatedFrequency);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FrequencyDto> getFrequencyById(Long id) {
        log.debug("Fetching frequency by ID: {}", id);
        return frequencyRepository.findById(id)
                .map(frequencyMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Frequency> getFrequencyEntityById(Long id) {
        return frequencyRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FrequencyDto> getFrequencyBySlug(String slug) {
        log.debug("Fetching frequency by slug: {}", slug);
        return frequencyRepository.findBySlug(slug)
                .map(frequencyMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FrequencyDto> getAllPublicFrequencies() {
        log.debug("Fetching all public frequencies");
        return frequencyRepository.findByIsPrivateFalse().stream()
                .map(frequencyMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FrequencyDto> getFrequenciesByOwner(Long ownerId) {
        log.debug("Fetching frequencies by owner ID: {}", ownerId);
        return frequencyRepository.findByOwnerId(ownerId).stream()
                .map(frequencyMapper::toDto)
                .toList();
    }

    @Override
    public void deleteFrequency(Long id) {
        log.debug("Deleting frequency with ID: {}", id);

        if (!frequencyRepository.existsById(id)) {
            throw new EndpointException("Frequency not found", HttpStatus.NOT_FOUND);
        }

        frequencyRepository.deleteById(id);
        log.info("Frequency deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canAccessFrequency(Long userId, Long frequencyId) {
        Frequency frequency = frequencyRepository.findById(frequencyId)
                .orElseThrow(() -> new EndpointException("Frequency not found", HttpStatus.NOT_FOUND));

        if (!frequency.isPrivate()) {
            return true;
        }

        return membershipRepository.existsByUserIdAndFrequencyId(userId, frequencyId);
    }
}

