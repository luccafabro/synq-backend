package com.synq.backend.controller;

import com.synq.backend.dto.response.ResponseDTO;
import com.synq.backend.dto.response.RoleDto;
import com.synq.backend.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for Role operations
 */
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Roles", description = "Role management API")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @Operation(summary = "Get all roles", description = "Retrieve all available roles in the system")
    public ResponseEntity<ResponseDTO<List<RoleDto>>> getAllRoles() {
        log.debug("GET /api/roles - Get all roles");
        
        List<RoleDto> roles = roleService.getAllRoles();
        
        return ResponseDTO.of(roles, org.springframework.http.HttpStatus.OK);
    }
}

