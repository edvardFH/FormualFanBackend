package com.onesquad.formulafan.adapter.dto;

import java.time.LocalDateTime;

public record ModerationResponseDTO(
        Long id,
        PostResponseDTO post,
        Long adminId,
        String adminUsername,
        String reason,
        LocalDateTime date) {
}
