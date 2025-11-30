package com.synq.backend.service;

import com.synq.backend.dto.request.CreateFrequencyDto;
import com.synq.backend.dto.request.UpdateFrequencyDto;
import com.synq.backend.dto.response.FrequencyDto;
import com.synq.backend.model.Frequency;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for Frequency operations
 */
public interface FrequencyService {

    /**
     * Create a new frequency
     */
    FrequencyDto createFrequency(CreateFrequencyDto dto, Long ownerId);

    /**
     * Update an existing frequency
     */
    FrequencyDto updateFrequency(Long id, UpdateFrequencyDto dto);

    /**
     * Get frequency by ID
     */
    Optional<FrequencyDto> getFrequencyById(Long id);

    /**
     * Get frequency entity by ID (for internal use)
     */
    Optional<Frequency> getFrequencyEntityById(Long id);

    /**
     * Get frequency by slug
     */
    Optional<FrequencyDto> getFrequencyBySlug(String slug);

    /**
     * Get all public frequencies
     */
    List<FrequencyDto> getAllPublicFrequencies();

    /**
     * Get frequencies by owner
     */
    List<FrequencyDto> getFrequenciesByOwner(Long ownerId);

    /**
     * Delete frequency by ID
     */
    void deleteFrequency(Long id);

    /**
     * Check if user can access frequency
     */
    boolean canAccessFrequency(Long userId, Long frequencyId);
}

