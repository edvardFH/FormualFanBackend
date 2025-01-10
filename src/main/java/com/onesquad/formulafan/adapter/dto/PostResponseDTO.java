package com.onesquad.formulafan.adapter.dto;


import java.time.LocalDateTime;

public record PostResponseDTO(
        Long id,
        String title,
        String description,
        String imageUrl,
        AuthorDTO author,
        GrandPrixDTO grandPrix,
        LocalDateTime dateCreated,
        int likeCount) {
}

