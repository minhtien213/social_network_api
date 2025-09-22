package com.example.social_network_api.repository;

import com.example.social_network_api.entity.PostMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostMediaRepository extends JpaRepository<PostMedia, Long> {
    List<PostMedia> findAllByPostId(Long postId);
}
