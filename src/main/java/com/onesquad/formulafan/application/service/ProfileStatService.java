package com.onesquad.formulafan.application.service;

import com.onesquad.formulafan.adapter.dto.ProfileStatDTO;
import org.springframework.stereotype.Service;

@Service
public class ProfileStatService {

    private final PostService postService;
    private final LikeService likeService;

    public ProfileStatService(PostService postService, LikeService likeService) {
        this.postService = postService;
        this.likeService = likeService;
    }

    public ProfileStatDTO getProfileStats(Long userId) {
        int totalPosts = postService.getTotalPostsByUser(userId);
        int totalLikesGiven = likeService.getTotalLikesGivenByUser(userId);
        int totalLikesReceived = likeService.getTotalLikesReceivedByUser(userId);

        return new ProfileStatDTO(totalPosts, totalLikesGiven, totalLikesReceived);
    }
}

