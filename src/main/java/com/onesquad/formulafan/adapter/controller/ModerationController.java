package com.onesquad.formulafan.adapter.controller;

import com.onesquad.formulafan.adapter.dto.ModerationRequestDTO;
import com.onesquad.formulafan.adapter.dto.ModerationResponseDTO;
import com.onesquad.formulafan.application.service.ModerationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/moderation")
public class ModerationController {

    private final ModerationService moderationService;

    public ModerationController(ModerationService moderationService) {
        this.moderationService = moderationService;
    }

    @PostMapping("/hide")
    public ResponseEntity<Void> hidePost(
            @RequestBody ModerationRequestDTO request,
            @RequestHeader("Authorization") String authorizationHeader) {
        moderationService.hidePost(request, authorizationHeader);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/hidden-posts")
    public ResponseEntity<List<ModerationResponseDTO>> getAllHiddenPosts() {
        return ResponseEntity.ok(moderationService.getAllHiddenPosts());
    }

    @PutMapping("/{moderationId}/reason")
    public ResponseEntity<Void> updateReason(
            @PathVariable("moderationId") Long moderationId,
            @RequestBody ModerationRequestDTO request,
            @RequestHeader("Authorization") String authorizationHeader) {
        moderationService.updateReason(moderationId, request, authorizationHeader);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{moderationId}/cancel")
    public ResponseEntity<Void> cancelHide(
            @PathVariable("moderationId") Long moderationId,
            @RequestHeader("Authorization") String authorizationHeader) {
        moderationService.cancelHide(moderationId, authorizationHeader);
        return ResponseEntity.noContent().build();
    }
}

