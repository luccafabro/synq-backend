-- V5: Create messages table
CREATE TABLE messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    external_id CHAR(36) NOT NULL UNIQUE,
    frequency_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    type VARCHAR(20) NOT NULL DEFAULT 'TEXT',
    reply_to_id BIGINT,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6),
    edited_at DATETIME(6),
    deleted_at DATETIME(6),
    metadata JSON,
    version BIGINT NOT NULL DEFAULT 0,
    active TINYINT(1) NOT NULL DEFAULT 1,

    INDEX idx_frequency_created (frequency_id, created_at),
    INDEX idx_author_id (author_id),
    INDEX idx_reply_to_id (reply_to_id),

    CONSTRAINT fk_messages_frequency
        FOREIGN KEY (frequency_id) REFERENCES frequencies(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_messages_author
        FOREIGN KEY (author_id) REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_messages_reply_to
        FOREIGN KEY (reply_to_id) REFERENCES messages(id)
        ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

