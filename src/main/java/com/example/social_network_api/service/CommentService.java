package com.example.social_network_api.service;

import com.example.social_network_api.dto.request.CommentRequestDTO;
import com.example.social_network_api.entity.Comment;

import java.util.List;

public interface CommentService extends IService<Comment> {
    Comment createComment(Long postId, CommentRequestDTO commentRequestDTO, String username);
    Comment updateComment(Long id, CommentRequestDTO commentRequestDTO, String username);
    List<Comment> getCommentsByPostId(Long postId);
    Long countCommentByPostId(Long postId);
}
