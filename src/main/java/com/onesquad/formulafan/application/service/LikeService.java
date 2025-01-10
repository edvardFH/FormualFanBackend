package com.onesquad.formulafan.application.service;

import com.onesquad.formulafan.adapter.persistence.Like;
import com.onesquad.formulafan.adapter.persistence.LikeRepository;
import com.onesquad.formulafan.adapter.persistence.Post;
import com.onesquad.formulafan.adapter.persistence.PostRepository;
import com.onesquad.formulafan.adapter.persistence.User;
import com.onesquad.formulafan.adapter.persistence.UserRepository;
import com.onesquad.formulafan.application.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public LikeService(
            LikeRepository likeRepository,
            PostRepository postRepository,
            UserRepository userRepository) {
        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public void addLike(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                                  .orElseThrow(() -> new ResourceNotFoundException(
                                          "Post not found with id: " + postId));
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new ResourceNotFoundException(
                                          "User not found with id: " + userId));

        if (likeRepository.existsByPostIdAndUserId(postId, userId)) {
            throw new IllegalArgumentException("User has already liked this post");
        }

        Like like = new Like(post, user);
        likeRepository.save(like);

        post.setLikeCount(post.getLikeCount() + 1);
        postRepository.save(post);
    }

    @Transactional
    public void removeLike(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                                  .orElseThrow(() -> new ResourceNotFoundException(
                                          "Post not found with id: " + postId));
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new ResourceNotFoundException(
                                          "User not found with id: " + userId));

        if (!likeRepository.existsByPostIdAndUserId(postId, userId)) {
            throw new IllegalArgumentException(
                    "Like not found for the given post and user");
        }

        likeRepository.deleteByPostIdAndUserId(postId, userId);

        post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
        postRepository.save(post);
    }

    @Transactional
    public void deleteLikesByPostId(Long postId) {
        likeRepository.deleteAllByPostId(postId);
    }


    public boolean isPostLikedByUser(Long postId, Long userId) {
        return likeRepository.existsByPostIdAndUserId(postId, userId);
    }
}
