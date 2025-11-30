package com.synq.backend.repository;

import com.synq.backend.model.Frequency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Frequency entity.
 */
@Repository
public interface FrequencyRepository extends JpaRepository<Frequency, Long> {

    /**
     * Find frequency by slug
     */
    Optional<Frequency> findBySlug(String slug);

    /**
     * Find all public frequencies
     */
    List<Frequency> findByIsPrivateFalse();

    /**
     * Find frequencies by owner
     */
    List<Frequency> findByOwnerId(Long ownerId);

    /**
     * Check if slug exists
     */
    boolean existsBySlug(String slug);
}

