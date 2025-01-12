package com.onesquad.formulafan.application.service;

import com.onesquad.formulafan.adapter.dto.AuthorDTO;
import com.onesquad.formulafan.adapter.dto.GrandPrixDTO;
import com.onesquad.formulafan.adapter.dto.PostRequestDTO;
import com.onesquad.formulafan.adapter.dto.PostResponseDTO;
import com.onesquad.formulafan.adapter.persistence.GrandPrix;
import com.onesquad.formulafan.adapter.persistence.GrandPrixRepository;
import com.onesquad.formulafan.adapter.persistence.LikeRepository;
import com.onesquad.formulafan.adapter.persistence.Post;
import com.onesquad.formulafan.adapter.persistence.PostRepository;
import com.onesquad.formulafan.adapter.persistence.User;
import com.onesquad.formulafan.adapter.persistence.UserRepository;
import com.onesquad.formulafan.application.exception.ResourceNotFoundException;
import com.onesquad.formulafan.security.AuthenticationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final GrandPrixRepository grandPrixRepository;
    private final LikeService likeService;
    private final LikeRepository likeRepository;
    private final AuthenticationService authenticationService;

    public PostService(
            PostRepository postRepository,
            UserRepository userRepository,
            GrandPrixRepository grandPrixRepository,
            LikeService likeService,
            LikeRepository likeRepository,
            AuthenticationService authenticationService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.grandPrixRepository = grandPrixRepository;
        this.likeService = likeService;
        this.likeRepository = likeRepository;
        this.authenticationService = authenticationService;
    }

    public PostResponseDTO createPost(PostRequestDTO request) {
        User user = userRepository.findById(request.userId())
                                  .orElseThrow(() -> new ResourceNotFoundException(
                                          "User not found with id: 1"));
        GrandPrix grandPrix = grandPrixRepository.findById(request.grandPrixId())
                                                 .orElseThrow(() -> new ResourceNotFoundException(
                                                         "Grand Prix not found " +
                                                                 "with id: " +
                                                                 request.grandPrixId()));
        Post post = new Post(request.title(),
                             request.description(),
                             LocalDateTime.now(),
                             request.imageUrl(),
                             user,
                             grandPrix,
                             0);
        Post savedPost = postRepository.save(post);
        return mapToResponseDTO(savedPost, null);
    }

    public List<PostResponseDTO> getAllPosts(String authorizationHeader) {
        User authenticatedUser =
                authenticationService.getAuthenticatedUser(authorizationHeader);

        return postRepository.findAllByOrderByDateCreatedDesc()
                             .stream()
                             .map(post -> mapToResponseDTO(post, authenticatedUser))
                             .collect(Collectors.toList());
    }

    public PostResponseDTO getPostById(Long id, String authorizationHeader) {
        User authenticatedUser =
                authenticationService.getAuthenticatedUser(authorizationHeader);

        Post post = postRepository.findById(id)
                                  .orElseThrow(() -> new ResourceNotFoundException(
                                          "Post not found with id: " + id));
        return mapToResponseDTO(post, authenticatedUser);
    }

    public PostResponseDTO updatePost(
            Long id, PostRequestDTO request, String authorizationHeader) {
        Post post = postRepository.findById(id)
                                  .orElseThrow(() -> new ResourceNotFoundException(
                                          "Post not found with id: " + id));
        GrandPrix grandPrix = grandPrixRepository.findById(request.grandPrixId())
                                                 .orElseThrow(() -> new ResourceNotFoundException(
                                                         "Grand Prix not found " +
                                                                 "with id: " +
                                                                 request.grandPrixId()));

        User authenticatedUser =
                authenticationService.getAuthenticatedUser(authorizationHeader);

        post.setTitle(request.title());
        post.setDescription(request.description());
        post.setImageUrl(request.imageUrl());
        post.setGrandPrix(grandPrix);
        Post updatedPost = postRepository.save(post);
        return mapToResponseDTO(updatedPost, authenticatedUser);
    }

    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                                  .orElseThrow(() -> new ResourceNotFoundException(
                                          "Post not found with id: " + id));
        postRepository.delete(post);
    }


    public void likePost(Long postId, Long userId) {
        likeService.addLike(postId, userId);
    }

    public void unlikePost(Long postId, Long userId) {
        likeService.removeLike(postId, userId);
    }

    public int getTotalPostsByUser(Long userId) {
        return postRepository.countByUserId(userId);
    }

    public List<PostResponseDTO> getPostsByGrandPrix(
            Long grandPrixId, String authorizationHeader) {
        User authenticatedUser =
                authenticationService.getAuthenticatedUser(authorizationHeader);

        return postRepository.findByGrandPrixIdOrderByDateCreatedDesc(grandPrixId)
                             .stream()
                             .map(post -> mapToResponseDTO(post, authenticatedUser))
                             .collect(Collectors.toList());
    }

    public List<PostResponseDTO> getPostsByUser(
            Long userId, String authorizationHeader) {
        User authenticatedUser =
                authenticationService.getAuthenticatedUser(authorizationHeader);

        return postRepository.findByUserIdOrderByDateCreatedDesc(userId)
                             .stream()
                             .map(post -> mapToResponseDTO(post, authenticatedUser))
                             .collect(Collectors.toList());
    }


    private PostResponseDTO mapToResponseDTO(Post post, User authenticatedUser) {
        boolean liked = false;

        if (authenticatedUser != null) {
            liked = likeRepository.existsByPostIdAndUserId(post.getId(),
                                                           authenticatedUser.getId());
        }

        AuthorDTO authorDTO =
                new AuthorDTO(post.getUser().getId(), post.getUser().getUsername());
        return new PostResponseDTO(post.getId(),
                                   post.getTitle(),
                                   post.getDescription(),
                                   post.getImageUrl(),
                                   authorDTO,
                                   mapToResponseDTO(post.getGrandPrix()),
                                   post.getDateCreated(),
                                   post.getLikeCount(),
                                   liked);
    }

    private GrandPrixDTO mapToResponseDTO(GrandPrix grandPrix) {
        return new GrandPrixDTO(grandPrix.getId(), grandPrix.getName());
    }
}

