package com.synq.backend.repository;

import com.synq.backend.model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Attachment entity.
 */
@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    /**
     * Find attachments by message
     */
    List<Attachment> findByMessageId(Long messageId);

    /**
     * Find attachments by uploader
     */
    List<Attachment> findByUploaderId(Long uploaderId);

    /**
     * Delete attachments by message
     */
    void deleteByMessageId(Long messageId);
}

