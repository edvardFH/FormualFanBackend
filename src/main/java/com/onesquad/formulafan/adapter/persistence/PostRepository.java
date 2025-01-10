package com.onesquad.formulafan.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByDateCreatedDesc();

    List<Post> findPostsByUserId(Long userId);

    int countByUserId(Long userId);
}
