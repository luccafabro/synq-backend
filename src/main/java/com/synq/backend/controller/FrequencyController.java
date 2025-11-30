package com.synq.backend.controller;

import com.synq.backend.dto.request.CreateFrequencyDto;
import com.synq.backend.dto.request.CreateMembershipDto;
import com.synq.backend.dto.request.CreateMessageDto;
import com.synq.backend.dto.request.UpdateFrequencyDto;
import com.synq.backend.dto.response.FrequencyDto;
import com.synq.backend.dto.response.MembershipDto;
import com.synq.backend.dto.response.MessageDto;
import com.synq.backend.dto.response.ResponseDTO;
import com.synq.backend.enums.MembershipRole;
import com.synq.backend.service.FrequencyService;
import com.synq.backend.service.MembershipService;
import com.synq.backend.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Frequency operations
 */
@RestController
@RequestMapping("/api/frequencies")
@RequiredArgsConstructor
@Tag(name = "Frequencies", description = "Frequency management endpoints")
public class FrequencyController {

    private final FrequencyService frequencyService;
    private final MembershipService membershipService;
    private final MessageService messageService;

    @PostMapping
    @Operation(summary = "Create a new frequency")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDTO<FrequencyDto>> createFrequency(
            @Valid @RequestBody CreateFrequencyDto dto,
            Authentication authentication) {
        // TODO: Extract user ID from JWT/Authentication
        Long ownerId = 1L; // Placeholder - should be extracted from authentication

        FrequencyDto frequency = frequencyService.createFrequency(dto, ownerId);
        return ResponseDTO.of(frequency, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all public frequencies")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDTO<List<FrequencyDto>>> getAllPublicFrequencies() {
        List<FrequencyDto> frequencies = frequencyService.getAllPublicFrequencies();
        return ResponseDTO.of(frequencies, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get frequency by ID")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDTO<FrequencyDto>> getFrequencyById(@PathVariable Long id) {
        return frequencyService.getFrequencyById(id)
                .map(frequency -> ResponseDTO.of(frequency, HttpStatus.OK))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get frequency by slug")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDTO<FrequencyDto>> getFrequencyBySlug(@PathVariable String slug) {
        return frequencyService.getFrequencyBySlug(slug)
                .map(frequency -> ResponseDTO.of(frequency, HttpStatus.OK))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update frequency")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDTO<FrequencyDto>> updateFrequency(
            @PathVariable Long id,
            @Valid @RequestBody UpdateFrequencyDto dto) {
        FrequencyDto frequency = frequencyService.updateFrequency(id, dto);
        return ResponseDTO.of(frequency, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete frequency")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDTO<Void>> deleteFrequency(@PathVariable Long id) {
        frequencyService.deleteFrequency(id);
        return ResponseDTO.of(null, HttpStatus.OK);
    }

    @PostMapping("/{id}/members")
    @Operation(summary = "Add member to frequency")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDTO<MembershipDto>> addMember(
            @PathVariable Long id,
            @Valid @RequestBody CreateMembershipDto dto) {
        MembershipDto membership = membershipService.joinFrequency(
                dto.userId(),
                id,
                dto.role() != null ? dto.role() : MembershipRole.MEMBER,
                dto.nickname()
        );
        return ResponseDTO.of(membership, HttpStatus.CREATED);
    }

    @GetMapping("/{id}/members")
    @Operation(summary = "Get members of a frequency")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDTO<List<MembershipDto>>> getMembers(@PathVariable Long id) {
        List<MembershipDto> members = membershipService.getMembershipsByFrequency(id);
        return ResponseDTO.of(members, HttpStatus.OK);
    }

    @PostMapping("/{id}/messages")
    @Operation(summary = "Post a message in a frequency")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDTO<MessageDto>> postMessage(
            @PathVariable Long id,
            @Valid @RequestBody CreateMessageDto dto,
            Authentication authentication) {
        // TODO: Extract user ID from JWT/Authentication
        Long authorId = 1L; // Placeholder

        MessageDto message = messageService.postMessage(dto, authorId);
        return ResponseDTO.of(message, HttpStatus.CREATED);
    }

    @GetMapping("/{id}/messages")
    @Operation(summary = "Get messages from a frequency")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDTO<List<MessageDto>>> getMessages(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<MessageDto> messages = messageService.getMessagesByFrequency(id, pageable);
        return ResponseDTO.of(messages, HttpStatus.OK);
    }
}

