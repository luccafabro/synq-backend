package com.synq.backend.service.impl;

import com.synq.backend.dto.request.CreateUserDto;
import com.synq.backend.dto.request.UpdateUserDto;
import com.synq.backend.dto.response.UserDto;
import com.synq.backend.enums.UserStatus;
import com.synq.backend.exceptions.EndpointException;
import com.synq.backend.mapper.UserMapper;
import com.synq.backend.model.User;
import com.synq.backend.repository.UserRepository;
import com.synq.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of UserService using SOLID principles and Java 17 features
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(CreateUserDto dto) {
        log.debug("Creating user with username: {}", dto.username());

        if (userRepository.existsByUsername(dto.username())) {
            throw new EndpointException("Username already exists", HttpStatus.CONFLICT);
        }

        if (userRepository.existsByEmail(dto.email())) {
            throw new EndpointException("Email already exists", HttpStatus.CONFLICT);
        }

        User user = userMapper.toEntity(dto);
        User savedUser = userRepository.save(user);

        log.info("User created successfully with ID: {}", savedUser.getId());
        return userMapper.toDto(savedUser);
    }

    @Override
    public UserDto updateUser(Long id, UpdateUserDto dto) {
        log.debug("Updating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new EndpointException("User not found", HttpStatus.NOT_FOUND));

        userMapper.updateEntity(dto, user);
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

        if (!userRepository.existsById(id)) {
            throw new EndpointException("User not found", HttpStatus.NOT_FOUND);
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
}

