-- V6: Create attachments table
CREATE TABLE attachments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    external_id CHAR(36) NOT NULL UNIQUE,
    message_id BIGINT NOT NULL,
    uploader_id BIGINT NOT NULL,
    file_url TEXT NOT NULL,
    mime_type VARCHAR(100),
    size_bytes BIGINT NOT NULL,
    storage_key VARCHAR(500),
    thumbnail_url TEXT,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6),
    version BIGINT NOT NULL DEFAULT 0,
    active TINYINT(1) NOT NULL DEFAULT 1,

    INDEX idx_message_id (message_id),
    INDEX idx_uploader_id (uploader_id),

    CONSTRAINT fk_attachments_message
        FOREIGN KEY (message_id) REFERENCES messages(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_attachments_uploader
        FOREIGN KEY (uploader_id) REFERENCES users(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

