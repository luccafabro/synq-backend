package com.synq.backend.service;

import com.synq.backend.AbstractIntegrationTest;
import com.synq.backend.dto.response.UserDto;
import com.synq.backend.model.User;
import com.synq.backend.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for UserService - tests user provisioning from Keycloak
 */
@Transactional
class UserServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should provision user from Keycloak on first login")
    void testProvisionUserFromKeycloak() {
        // Given
        String keycloakExternalId = "keycloak-sub-12345";
        String username = "testuser";
        String email = "test@example.com";

        // When
        User user = userService.provisionUserFromKeycloak(keycloakExternalId, username, email);

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getId()).isNotNull();
        assertThat(user.getKeycloakExternalId()).isEqualTo(keycloakExternalId);
        assertThat(user.getUsername()).isEqualTo(username);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getLastLoginAt()).isNotNull();

        // Verify it was persisted
        User foundUser = userRepository.findByKeycloakExternalId(keycloakExternalId).orElse(null);
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("Should return existing user on subsequent Keycloak login")
    void testProvisionExistingUser() {
        // Given - create initial user
        String keycloakExternalId = "keycloak-sub-67890";
        String username = "existinguser";
        String email = "existing@example.com";

        User firstUser = userService.provisionUserFromKeycloak(keycloakExternalId, username, email);
        Long firstUserId = firstUser.getId();

        // When - provision again (simulating second login)
        User secondUser = userService.provisionUserFromKeycloak(keycloakExternalId, username, email);

        // Then - should return same user
        assertThat(secondUser.getId()).isEqualTo(firstUserId);
        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should find user by Keycloak external ID")
    void testGetUserByKeycloakExternalId() {
        // Given
        String keycloakExternalId = "keycloak-sub-find-test";
        User user = userService.provisionUserFromKeycloak(keycloakExternalId, "finduser", "find@example.com");

        // When
        UserDto foundUser = userService.getUserByKeycloakExternalId(keycloakExternalId).orElse(null);

        // Then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.keycloakExternalId()).isEqualTo(keycloakExternalId);
        assertThat(foundUser.id()).isEqualTo(user.getId());
    }
}

