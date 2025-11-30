-- V7: Create invites table
CREATE TABLE invites (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    external_id CHAR(36) NOT NULL UNIQUE,
    frequency_id BIGINT NOT NULL,
    inviter_id BIGINT NOT NULL,
    token VARCHAR(100) NOT NULL UNIQUE,
    expires_at DATETIME(6) NOT NULL,
    max_uses INT NOT NULL DEFAULT 1,
    uses_count INT NOT NULL DEFAULT 0,
    role_on_join VARCHAR(20) NOT NULL DEFAULT 'MEMBER',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6),
    version BIGINT NOT NULL DEFAULT 0,
    active TINYINT(1) NOT NULL DEFAULT 1,

    INDEX idx_frequency_id (frequency_id),
    INDEX idx_inviter_id (inviter_id),
    INDEX idx_token (token),

    CONSTRAINT fk_invites_frequency
        FOREIGN KEY (frequency_id) REFERENCES frequencies(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_invites_inviter
        FOREIGN KEY (inviter_id) REFERENCES users(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

