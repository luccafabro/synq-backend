-- V3: Create frequencies table
CREATE TABLE frequencies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    external_id CHAR(36) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    is_private TINYINT(1) NOT NULL DEFAULT 0,
    owner_id BIGINT NOT NULL,
    settings JSON,
    cover_image_url VARCHAR(500),
    max_participants INT NOT NULL DEFAULT 1000,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6),
    version BIGINT NOT NULL DEFAULT 0,
    active TINYINT(1) NOT NULL DEFAULT 1,

    INDEX idx_slug (slug),
    INDEX idx_owner_id (owner_id),

    CONSTRAINT fk_frequencies_owner
        FOREIGN KEY (owner_id) REFERENCES users(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

