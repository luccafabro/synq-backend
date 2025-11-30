package com.synq.backend.repository;

import com.synq.backend.model.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for Message entity.
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Find messages by frequency ordered by creation date descending (cursor-based pagination)
     */
    List<Message> findTop50ByFrequencyIdAndCreatedAtBeforeOrderByCreatedAtDesc(
            Long frequencyId,
            LocalDateTime before
    );

    /**
     * Find messages by frequency with pagination
     */
    List<Message> findByFrequencyIdOrderByCreatedAtDesc(Long frequencyId, Pageable pageable);

    /**
     * Find messages by author
     */
    List<Message> findByAuthorIdOrderByCreatedAtDesc(Long authorId, Pageable pageable);

    /**
     * Find replies to a message
     */
    List<Message> findByReplyToIdOrderByCreatedAtAsc(Long replyToId);

    /**
     * Count messages in a frequency
     */
    long countByFrequencyId(Long frequencyId);

    /**
     * Find messages by frequency and author
     */
    List<Message> findByFrequencyIdAndAuthorId(Long frequencyId, Long authorId, Pageable pageable);

    /**
     * Find non-deleted messages by frequency
     */
    @Query("SELECT m FROM Message m WHERE m.frequency.id = :frequencyId AND m.deletedAt IS NULL ORDER BY m.createdAt DESC")
    List<Message> findActiveMessagesByFrequencyId(@Param("frequencyId") Long frequencyId, Pageable pageable);
}

