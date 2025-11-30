-- V2: Create users table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    external_id CHAR(36) NOT NULL UNIQUE,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    display_name VARCHAR(150),
    avatar_url VARCHAR(500),
    email_verified TINYINT(1) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    keycloak_external_id VARCHAR(255),
    metadata JSON,
    last_login_at DATETIME(6),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6),
    version BIGINT NOT NULL DEFAULT 0,
    active TINYINT(1) NOT NULL DEFAULT 1,

    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_keycloak_external_id (keycloak_external_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

