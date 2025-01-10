package com.onesquad.formulafan.application.service;

import com.onesquad.formulafan.adapter.dto.AuthorDTO;
import com.onesquad.formulafan.adapter.dto.GrandPrixDTO;
import com.onesquad.formulafan.adapter.dto.PostRequestDTO;
import com.onesquad.formulafan.adapter.dto.PostResponseDTO;
import com.onesquad.formulafan.adapter.persistence.GrandPrix;
import com.onesquad.formulafan.adapter.persistence.GrandPrixRepository;
import com.onesquad.formulafan.adapter.persistence.Post;
import com.onesquad.formulafan.adapter.persistence.PostRepository;
import com.onesquad.formulafan.adapter.persistence.User;
import com.onesquad.formulafan.adapter.persistence.UserRepository;
import com.onesquad.formulafan.application.exception.ResourceNotFoundException;
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

    public PostService(
            PostRepository postRepository,
            UserRepository userRepository,
            GrandPrixRepository grandPrixRepository,
            LikeService likeService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.grandPrixRepository = grandPrixRepository;
        this.likeService = likeService;
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
        Post post = new Post(
                request.title(),
                request.description(),
                LocalDateTime.now(),
                request.imageUrl(),
                user,
                grandPrix,
                0);
        Post savedPost = postRepository.save(post);
        return mapToResponseDTO(savedPost);
    }

    public List<PostResponseDTO> getAllPosts() {
        return postRepository.findAllByOrderByDateCreatedDesc()
                             .stream()
                             .map(this::mapToResponseDTO)
                             .collect(Collectors.toList());
    }

    public PostResponseDTO getPostById(Long id) {
        Post post = postRepository.findById(id)
                                  .orElseThrow(() -> new ResourceNotFoundException(
                                          "Post not found with id: " + id));
        return mapToResponseDTO(post);
    }

    public PostResponseDTO updatePost(Long id, PostRequestDTO request) {
        Post post = postRepository.findById(id)
                                  .orElseThrow(() -> new ResourceNotFoundException(
                                          "Post not found with id: " + id));
        GrandPrix grandPrix = grandPrixRepository.findById(request.grandPrixId())
                                                 .orElseThrow(() -> new ResourceNotFoundException(
                                                         "Grand Prix not found " +
                                                                 "with id: " +
                                                                 request.grandPrixId()));
        post.setTitle(request.title());
        post.setDescription(request.description());
        post.setImageUrl(request.imageUrl());
        post.setGrandPrix(grandPrix);
        Post updatedPost = postRepository.save(post);
        return mapToResponseDTO(updatedPost);
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

    public List<PostResponseDTO> getPostsByGrandPrix(Long grandPrixId) {
        return postRepository.findByGrandPrixIdOrderByDateCreatedDesc(grandPrixId)
                             .stream()
                             .map(this::mapToResponseDTO)
                             .collect(Collectors.toList());
    }


    private PostResponseDTO mapToResponseDTO(Post post) {
        AuthorDTO authorDTO =
                new AuthorDTO(post.getUser().getId(), post.getUser().getUsername());
        return new PostResponseDTO(
                post.getId(),
                post.getTitle(),
                post.getDescription(),
                post.getImageUrl(),
                authorDTO,
                mapToResponseDTO(post.getGrandPrix()),
                post.getDateCreated(),
                post.getLikeCount());
    }

    private GrandPrixDTO mapToResponseDTO(GrandPrix grandPrix) {
        return new GrandPrixDTO(grandPrix.getId(), grandPrix.getName());
    }
}

