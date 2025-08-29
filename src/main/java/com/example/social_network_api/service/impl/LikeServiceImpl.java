package com.example.social_network_api.service.impl;

import com.example.social_network_api.entity.Like;
import com.example.social_network_api.entity.Notification;
import com.example.social_network_api.entity.Post;
import com.example.social_network_api.entity.User;
import com.example.social_network_api.exception.custom.BadRequestException;
import com.example.social_network_api.exception.custom.ResourceNotFoundException;
import com.example.social_network_api.repository.LikeRepository;
import com.example.social_network_api.service.LikeService;
import com.example.social_network_api.service.NotificationService;
import com.example.social_network_api.service.PostService;
import com.example.social_network_api.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final PostService postService;
    private final UserService userService;
    private final NotificationService notificationService;

    @Transactional
    public Like createLike(Long postId, String username) {
        Post post = postService.findById(postId);
        User user = userService.findByUsername(username);
        if (likeRepository.existsByPostIdAndUserId(postId, user.getId())) {
            throw new BadRequestException("Like already exists");
        }
        Like like = Like.builder()
                .post(post)
                .user(user)
                .build();
        Like savedLike = likeRepository.save(like);
        notificationService.createAndSentNotification(savedLike.getId(), post.getUser(), Notification.NotificationType.LIKE);
        return savedLike;
    }

    @Override
    @Transactional
    public void unLikePostId(Long postId, String username) {
        Post post = postService.findById(postId);
        if (post == null) {
            throw new ResourceNotFoundException("Post not found");
        }

        User user = userService.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }

        if (!likeRepository.existsByPostIdAndUserId(postId, user.getId())) {
            throw new BadRequestException("Like not found or UnAuthorized");
        }

        likeRepository.deleteByPostIdAndUserId(postId, user.getId());
    }

    @Override
    public void deleteById(Long id) {
        if (!likeRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Like not found");
        }
        likeRepository.deleteById(id);
    }

    @Override
    public Page<Like> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return likeRepository.findAll(pageable);
    }

    @Override
    public Like findById(Long id) {
        return likeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Like not found")
                );
    }

    @Override
    public List<String> getUsernameLikedPost(Long postId) {
        Post existingPost = postService.findById(postId);
        List<Like> likes = likeRepository.findAllByPostId(postId);
        return likes.stream().map(like -> like.getUser().getUsername()).collect(Collectors.toList());
    }

    @Override
    public Map<String, Long> getLikedPostCount(Long postId) {
        Post existingPost = postService.findById(postId);
        Long count = likeRepository.getLikedPostCount(postId);
        return Map.of("likeCount", count);
    }

    @Override
    public Map<String, Boolean> existsByPostIdAndUserId(Long postId, String username) {
        Post existingPost = postService.findById(postId);
        User user = userService.findByUsername(username);
        Boolean liked = likeRepository.existsByPostIdAndUserId(postId, user.getId());
        return Map.of("isLiked", liked);
    }
}
