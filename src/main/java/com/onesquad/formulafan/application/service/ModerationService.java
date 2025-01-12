package com.onesquad.formulafan.application.service;

import com.onesquad.formulafan.adapter.dto.ModerationRequestDTO;
import com.onesquad.formulafan.adapter.dto.ModerationResponseDTO;
import com.onesquad.formulafan.adapter.dto.PostResponseDTO;
import com.onesquad.formulafan.adapter.persistence.Moderation;
import com.onesquad.formulafan.adapter.persistence.ModerationRepository;
import com.onesquad.formulafan.adapter.persistence.Post;
import com.onesquad.formulafan.adapter.persistence.PostRepository;
import com.onesquad.formulafan.adapter.persistence.Role;
import com.onesquad.formulafan.adapter.persistence.User;
import com.onesquad.formulafan.application.exception.IllegalOperationException;
import com.onesquad.formulafan.application.exception.ResourceNotFoundException;
import com.onesquad.formulafan.security.AuthenticationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ModerationService {

    private final ModerationRepository moderationRepository;
    private final PostRepository postRepository;
    private final AuthenticationService authenticationService;
    private final PostService postService;

    public ModerationService(
            ModerationRepository moderationRepository,
            PostRepository postRepository,
            AuthenticationService authenticationService,
            PostService postService) {
        this.moderationRepository = moderationRepository;
        this.postRepository = postRepository;
        this.authenticationService = authenticationService;
        this.postService = postService;
    }

    @Transactional
    public void hidePost(ModerationRequestDTO request, String authorizationHeader) {
        User admin = validateAdmin(authorizationHeader);
        Post post = postRepository.findById(request.postId())
                                  .orElseThrow(() -> new ResourceNotFoundException(
                                          "Post not found with id: " +
                                                  request.postId()));

        post.setHidden(true);
        postRepository.save(post);

        Moderation moderation =
                new Moderation(post, admin, request.reason(), LocalDateTime.now());
        moderationRepository.save(moderation);
    }

    public List<ModerationResponseDTO> getAllHiddenPosts(String authorizationHeader) {
        User authenticatedUser =
                authenticationService.getAuthenticatedUser(authorizationHeader);

        return moderationRepository.findAll()
                                   .stream()
                                   .map(moderation -> mapToResponseDTO(moderation,
                                                                       authenticatedUser))
                                   .collect(Collectors.toList());
    }

    private ModerationResponseDTO mapToResponseDTO(
            Moderation moderation, User authenticatedUser) {
        PostResponseDTO postResponseDTO =
                postService.mapToResponseDTO(moderation.getPost(),
                                             authenticatedUser);

        return new ModerationResponseDTO(moderation.getId(),
                                         postResponseDTO,
                                         moderation.getAdmin().getId(),
                                         moderation.getAdmin().getUsername(),
                                         moderation.getReason(),
                                         moderation.getDate());
    }

    @Transactional
    public void updateReason(
            Long moderationId,
            ModerationRequestDTO request,
            String authorizationHeader) {
        validateAdmin(authorizationHeader);

        Moderation moderation = moderationRepository.findById(moderationId)
                                                    .orElseThrow(() -> new ResourceNotFoundException(
                                                            "Moderation entry not " +
                                                                    "found with " +
                                                                    "id: " +
                                                                    moderationId));

        moderation.setReason(request.reason());
        moderationRepository.save(moderation);
    }


    @Transactional
    public void cancelHide(Long moderationId, String authorizationHeader) {
        validateAdmin(authorizationHeader);

        Moderation moderation = moderationRepository.findById(moderationId)
                                                    .orElseThrow(() -> new ResourceNotFoundException(
                                                            "Moderation entry not " +
                                                                    "found with " +
                                                                    "id: " +
                                                                    moderationId));

        Post post = moderation.getPost();
        post.setHidden(false);
        postRepository.save(post);

        moderationRepository.deleteById(moderationId);
    }

    private User validateAdmin(String authorizationHeader) {
        User admin = authenticationService.getAuthenticatedUser(authorizationHeader);

        if (admin == null || !admin.getRole().equals(Role.ADMIN)) {
            throw new IllegalOperationException(
                    "You do not have permission to perform this action");
        }

        return admin;
    }
}

