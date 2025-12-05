package com.synq.backend.service.impl;

import com.synq.backend.dto.request.CreateUserDto;
import com.synq.backend.dto.request.UpdateUserDto;
import com.synq.backend.dto.response.UserDto;
import com.synq.backend.enums.UserRole;
import com.synq.backend.enums.UserStatus;
import com.synq.backend.exceptions.EndpointException;
import com.synq.backend.mapper.UserMapper;
import com.synq.backend.model.Role;
import com.synq.backend.model.User;
import com.synq.backend.repository.RoleRepository;
import com.synq.backend.repository.UserRepository;
import com.synq.backend.service.KeycloakService;
import com.synq.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Implementation of UserService using SOLID principles and Java 17 features
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final KeycloakService keycloakService;

    @Override
    public UserDto createUser(CreateUserDto dto) {
        log.debug("Creating user with username: {}", dto.username());

        if (userRepository.existsByUsername(dto.username())) {
            throw new EndpointException("Username already exists", HttpStatus.CONFLICT);
        }

        if (userRepository.existsByEmail(dto.email())) {
            throw new EndpointException("Email already exists", HttpStatus.CONFLICT);
        }

        try {
            String keycloakUserId = keycloakService.createUser(
                    dto.username(),
                    dto.email(),
                    dto.firstName(),
                    dto.lastName()
            );

            log.info("User created in Keycloak with ID: {}", keycloakUserId);

            User user = userMapper.toEntity(dto);
            user.setKeycloakExternalId(keycloakUserId);

            // Assign roles - default to USER role if none provided
            Set<Role> roles = assignRoles(dto.roles() != null && !dto.roles().isEmpty()
                    ? dto.roles()
                    : Set.of(UserRole.USER));
            user.setRoles(roles);

            User savedUser = userRepository.save(user);

            log.info("User created successfully with ID: {} and roles: {}",
                    savedUser.getId(),
                    savedUser.getRoles().stream().map(r -> r.getName().name()).toList());
            return userMapper.toDto(savedUser);

        } catch (Exception e) {
            log.error("Error creating user: {}", dto.username(), e);
            throw e;
        }
    }

    @Override
    public UserDto updateUser(Long id, UpdateUserDto dto) {
        log.debug("Updating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new EndpointException("User not found", HttpStatus.NOT_FOUND));

        userMapper.updateEntity(dto, user);

        // Update roles if provided
        if (dto.roles() != null && !dto.roles().isEmpty()) {
            Set<Role> roles = assignRoles(dto.roles());
            user.setRoles(roles);
            log.debug("Updated roles for user ID {}: {}", id, dto.roles());
        }

        if (user.getKeycloakExternalId() != null) {
            try {
                keycloakService.updateUser(
                        user.getKeycloakExternalId(),
                        dto.email(),
                        dto.firstName(),
                        dto.lastName()
                );
                log.info("User updated in Keycloak: {}", user.getKeycloakExternalId());
            } catch (Exception e) {
                log.error("Failed to update user in Keycloak, but continuing with database update", e);
            }
        }

        User updatedUser = userRepository.save(user);

        log.info("User updated successfully with ID: {}", id);
        return userMapper.toDto(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDto> getUserById(Long id) {
        log.debug("Fetching user by ID: {}", id);
        return userRepository.findById(id)
                .map(userMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserEntityById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDto> getUserByKeycloakExternalId(String keycloakExternalId) {
        log.debug("Fetching user by Keycloak external ID: {}", keycloakExternalId);
        return userRepository.findByKeycloakExternalId(keycloakExternalId)
                .map(userMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserEntityByKeycloakExternalId(String keycloakExternalId) {
        return userRepository.findByKeycloakExternalId(keycloakExternalId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDto> getUserByUsername(String username) {
        log.debug("Fetching user by username: {}", username);
        return userRepository.findByUsername(username)
                .map(userMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        log.debug("Fetching all users");
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public void deleteUser(Long id) {
        log.debug("Deleting user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new EndpointException("User not found", HttpStatus.NOT_FOUND));

        if (user.getKeycloakExternalId() != null) {
            try {
                keycloakService.deleteUser(user.getKeycloakExternalId());
                log.info("User deleted from Keycloak: {}", user.getKeycloakExternalId());
            } catch (Exception e) {
                log.error("Failed to delete user from Keycloak, but continuing with database deletion", e);
            }
        }

        userRepository.deleteById(id);
        log.info("User deleted successfully with ID: {}", id);
    }

    @Override
    public User provisionUserFromKeycloak(String keycloakExternalId, String username, String email) {
        log.debug("Provisioning user from Keycloak: {}", keycloakExternalId);

        return userRepository.findByKeycloakExternalId(keycloakExternalId)
                .orElseGet(() -> {
                    log.info("Creating new user from Keycloak: {}", username);

                    User newUser = User.builder()
                            .keycloakExternalId(keycloakExternalId)
                            .username(username)
                            .email(email)
                            .displayName(username)
                            .emailVerified(false)
                            .status(UserStatus.ACTIVE)
                            .lastLoginAt(LocalDateTime.now())
                            .roles(assignRoles(Set.of(UserRole.USER)))
                            .build();

                    return userRepository.save(newUser);
                });
    }

    @Override
    public void updateLastLogin(Long userId) {
        log.debug("Updating last login for user ID: {}", userId);

        userRepository.findById(userId).ifPresent(user -> {
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);
        });
    }

    /**
     * Assign roles to user by fetching them from database
     * Creates roles if they don't exist
     */
    private Set<Role> assignRoles(Set<UserRole> roleNames) {
        Set<Role> roles = new HashSet<>();

        for (UserRole roleName : roleNames) {
            Role role = roleRepository.findByName(roleName)
                    .orElseGet(() -> {
                        log.warn("Role {} not found in database, creating it", roleName);
                        Role newRole = new Role(roleName, roleName.getDescription());
                        return roleRepository.save(newRole);
                    });
            roles.add(role);
        }

        return roles;
    }
}

