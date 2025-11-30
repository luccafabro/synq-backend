package com.synq.backend.controller;

import com.synq.backend.dto.request.CreateUserDto;
import com.synq.backend.dto.request.UpdateUserDto;
import com.synq.backend.dto.response.ResponseDTO;
import com.synq.backend.dto.response.UserDto;
import com.synq.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for User operations
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management endpoints")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "Create a new user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDTO<UserDto>> createUser(@Valid @RequestBody CreateUserDto dto) {
        UserDto user = userService.createUser(dto);
        return ResponseDTO.of(user, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDTO<UserDto>> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> ResponseDTO.of(user, HttpStatus.OK))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "Get user by username")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDTO<UserDto>> getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username)
                .map(user -> ResponseDTO.of(user, HttpStatus.OK))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get all users")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDTO<List<UserDto>>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseDTO.of(users, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDTO<UserDto>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserDto dto) {
        UserDto user = userService.updateUser(id, dto);
        return ResponseDTO.of(user, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDTO<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseDTO.of(null, HttpStatus.OK);
    }
}

