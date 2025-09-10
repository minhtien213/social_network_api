package com.example.social_network_api.service.impl;

import com.example.social_network_api.dto.request.CommentRequestDTO;
import com.example.social_network_api.entity.Comment;
import com.example.social_network_api.entity.Notification;
import com.example.social_network_api.entity.Post;
import com.example.social_network_api.entity.User;
import com.example.social_network_api.exception.custom.BadRequestException;
import com.example.social_network_api.exception.custom.ResourceNotFoundException;
import com.example.social_network_api.exception.custom.UnauthorizedException;
import com.example.social_network_api.mapper.CommentMapper;
import com.example.social_network_api.repository.CommentRepository;
import com.example.social_network_api.service.CommentService;
import com.example.social_network_api.service.NotificationService;
import com.example.social_network_api.service.PostService;
import com.example.social_network_api.service.UserService;
import com.example.social_network_api.utils.AuthUtils;
import com.example.social_network_api.utils.UploadsUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;
    private final UserService userService;
    private final CommentMapper commentMapper;
    private final NotificationService notificationService;

    @Transactional
    @Override
    public Comment createComment(Long postId, CommentRequestDTO commentRequestDTO, String username) {
        Post post = postService.findById(postId);
        if (post == null) {
            throw new ResourceNotFoundException("Post Not Found");
        }
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("Username Not Found");
        }
        if ((commentRequestDTO.getContent() == null || commentRequestDTO.getContent().isBlank())
                && (commentRequestDTO.getMediaUrl() == null || commentRequestDTO.getMediaUrl().isEmpty())) {
            throw new BadRequestException("Content or mediaUrl is required");
        }

        String mediaUrl = UploadsUtils.uploadFile(commentRequestDTO.getMediaUrl());
        Comment comment = commentMapper.toComment(post, user, commentRequestDTO, mediaUrl);
        Comment savedComment = commentRepository.save(comment);
        notificationService.createAndSentNotification(savedComment.getId(), post.getUser(), Notification.NotificationType.COMMENT);
        return savedComment;
    }

    @Transactional
    @Override
    public Comment updateComment(Long id, CommentRequestDTO commentRequestDTO, String username) {
        Comment existingComment = this.findById(id);
        if(existingComment == null) {
            throw new ResourceNotFoundException("Comment Not Found");
        }
        if ((commentRequestDTO.getContent() == null || commentRequestDTO.getContent().isBlank())
                && (commentRequestDTO.getMediaUrl() == null || commentRequestDTO.getMediaUrl().isEmpty())) {
            throw new BadRequestException("Content or mediaUrl is required");
        }
        if(!existingComment.getUser().getUsername().equals(username) && !AuthUtils.isAdmin()) {
            throw new UnauthorizedException("Unauthorized");
        }
        String mediaUrl = UploadsUtils.uploadFile(commentRequestDTO.getMediaUrl());
        existingComment.setContent(commentRequestDTO.getContent());
        existingComment.setMediaUrl(mediaUrl);
        existingComment.setUpdatedAt(LocalDateTime.now());
        return commentRepository.save(existingComment);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Comment not found with id " + id)
        );
        commentRepository.deleteById(id);
    }

    @Override
    public Page<Comment> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return commentRepository.findAll(pageable);
    }

    @Override
    public Comment findById(Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Comment not found with id " + id)
        );
        return comment;
    }

    @Override
    public List<Comment> getCommentsByPostId(Long postId) {
        if(!commentRepository.existsByPost_Id(postId)) {
            throw new ResourceNotFoundException("Post Not Found");
        }
        return commentRepository.findAllByPost_Id(postId);
    }
}
