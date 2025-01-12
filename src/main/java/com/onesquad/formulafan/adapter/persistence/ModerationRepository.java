package com.onesquad.formulafan.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModerationRepository extends JpaRepository<Moderation, Long> {
    List<Moderation> findByPostId(Long postId);
}
