-- V4: Create memberships table
CREATE TABLE memberships (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    external_id CHAR(36) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    frequency_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'MEMBER',
    joined_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    muted_until DATETIME(6),
    is_banned TINYINT(1) NOT NULL DEFAULT 0,
    nickname VARCHAR(100),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6),
    version BIGINT NOT NULL DEFAULT 0,
    active TINYINT(1) NOT NULL DEFAULT 1,

    INDEX idx_user_id (user_id),
    INDEX idx_frequency_id (frequency_id),

    CONSTRAINT uk_user_frequency UNIQUE (user_id, frequency_id),

    CONSTRAINT fk_memberships_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_memberships_frequency
        FOREIGN KEY (frequency_id) REFERENCES frequencies(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

