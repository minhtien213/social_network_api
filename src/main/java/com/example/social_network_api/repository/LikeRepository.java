package com.example.social_network_api.repository;

import com.example.social_network_api.entity.Like;
import com.example.social_network_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> deleteByPostIdAndUserId(Long postId, Long userId);
    Boolean existsByPostIdAndUserId(Long postId, Long userId);
    List<Like> findAllByPostId(Long postId);
    @Query("SELECT count(l) From Like l where l.post.id = :postId ")
    Long getLikedPostCount(Long postId);
}
