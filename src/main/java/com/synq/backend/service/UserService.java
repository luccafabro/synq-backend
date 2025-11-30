package com.synq.backend.service;

import com.synq.backend.dto.request.CreateUserDto;
import com.synq.backend.dto.request.UpdateUserDto;
import com.synq.backend.dto.response.UserDto;
import com.synq.backend.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for User operations
 */
public interface UserService {

    /**
     * Create a new user
     */
    UserDto createUser(CreateUserDto dto);

    /**
     * Update an existing user
     */
    UserDto updateUser(Long id, UpdateUserDto dto);

    /**
     * Get user by ID
     */
    Optional<UserDto> getUserById(Long id);

    /**
     * Get user entity by ID (for internal use)
     */
    Optional<User> getUserEntityById(Long id);

    /**
     * Get user by Keycloak external ID
     */
    Optional<UserDto> getUserByKeycloakExternalId(String keycloakExternalId);

    /**
     * Get user entity by Keycloak external ID (for internal use)
     */
    Optional<User> getUserEntityByKeycloakExternalId(String keycloakExternalId);

    /**
     * Get user by username
     */
    Optional<UserDto> getUserByUsername(String username);

    /**
     * Get all users
     */
    List<UserDto> getAllUsers();

    /**
     * Delete user by ID
     */
    void deleteUser(Long id);

    /**
     * Provision user from Keycloak (find or create)
     */
    User provisionUserFromKeycloak(String keycloakExternalId, String username, String email);

    /**
     * Update last login timestamp
     */
    void updateLastLogin(Long userId);
}

