package com.synq.backend.service;

import com.synq.backend.model.User;
import com.synq.backend.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

/**
 * Service for Keycloak integration.
 * Handles user provisioning from JWT claims.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakService {

    private final UserServiceImpl userService;

    /**
     * Provision user from JWT token (find or create)
     *
     * @param jwt JWT token from Keycloak
     * @return User entity
     */
    public User provisionUserFromJwt(Jwt jwt) {
        String keycloakExternalId = jwt.getSubject(); // "sub" claim
        String username = jwt.getClaim("preferred_username");
        String email = jwt.getClaim("email");

        log.debug("Provisioning user from JWT: sub={}, username={}", keycloakExternalId, username);

        return userService.provisionUserFromKeycloak(keycloakExternalId, username, email);
    }

    /**
     * Extract user ID from JWT
     */
    public String getUserIdFromJwt(Jwt jwt) {
        return jwt.getSubject();
    }

    /**
     * Extract username from JWT
     */
    public String getUsernameFromJwt(Jwt jwt) {
        return jwt.getClaim("preferred_username");
    }

    /**
     * Extract email from JWT
     */
    public String getEmailFromJwt(Jwt jwt) {
        return jwt.getClaim("email");
    }
}

