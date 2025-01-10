package com.onesquad.formulafan.adapter.dto;

public record PostRequestDTO(
        String title,
        String description,
        String imageUrl,
        Long userId,
        Long grandPrixId) {
}

