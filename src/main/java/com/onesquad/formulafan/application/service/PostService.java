package com.onesquad.formulafan.application.service;

import com.onesquad.formulafan.adapter.dto.AuthorDTO;
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

    public PostService(
            PostRepository postRepository,
            UserRepository userRepository,
            GrandPrixRepository grandPrixRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.grandPrixRepository = grandPrixRepository;
    }

    public PostResponseDTO createPost(PostRequestDTO request) {
        User user =
                userRepository.findById(1L) // Replace with the logged-in user's ID
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
        if (!postRepository.existsById(id)) {
            throw new ResourceNotFoundException("Post not found with id: " + id);
        }
        postRepository.deleteById(id);
    }

    public PostResponseDTO incrementLikeCount(Long id) {
        Post post = postRepository.findById(id)
                                  .orElseThrow(() -> new ResourceNotFoundException(
                                          "Post not found with id: " + id));
        post.setLikeCount(post.getLikeCount() + 1);
        Post updatedPost = postRepository.save(post);
        return mapToResponseDTO(updatedPost);
    }

    public PostResponseDTO decrementLikeCount(Long id) {
        Post post = postRepository.findById(id)
                                  .orElseThrow(() -> new ResourceNotFoundException(
                                          "Post not found with id: " + id));
        post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
        Post updatedPost = postRepository.save(post);
        return mapToResponseDTO(updatedPost);
    }

    private PostResponseDTO mapToResponseDTO(Post post) {
        AuthorDTO authorDTO =
                new AuthorDTO(post.getUser().getId(), post.getUser().getUsername());
        return new PostResponseDTO(post.getId(),
                                   post.getTitle(),
                                   post.getDescription(),
                                   post.getImageUrl(),
                                   authorDTO,
                                   post.getGrandPrix().getId(),
                                   post.getDateCreated(),
                                   post.getLikeCount());
    }
}

