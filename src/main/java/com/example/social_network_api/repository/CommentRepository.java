package com.example.social_network_api.repository;

import com.example.social_network_api.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPost_Id(Long postId);
    Boolean existsByPost_Id(Long postId);

    @Query("select count(m) from Comment m where m.post.id = :postId")
    Long countCommentByPostId(@Param("postId") Long postId);
}
