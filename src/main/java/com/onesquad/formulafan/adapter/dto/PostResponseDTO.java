package com.onesquad.formulafan.adapter.dto;


import java.time.LocalDateTime;

public record PostResponseDTO(
        Long id,
        String title,
        String description,
        String imageUrl,
        AuthorDTO author,
        Long grandPrixId,
        LocalDateTime dateCreated,
        int likeCount) {
}

