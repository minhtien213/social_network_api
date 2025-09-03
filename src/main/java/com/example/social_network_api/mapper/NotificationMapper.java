package com.example.social_network_api.mapper;

import com.example.social_network_api.dto.respone.NotificationResponseDTO;
import com.example.social_network_api.entity.Notification;
import com.example.social_network_api.entity.User;
import com.example.social_network_api.exception.custom.ResourceNotFoundException;
import com.example.social_network_api.repository.CommentRepository;
import com.example.social_network_api.repository.FollowRepository;
import com.example.social_network_api.repository.LikeRepository;
import com.example.social_network_api.service.CommentService;
import com.example.social_network_api.service.FollowService;
import com.example.social_network_api.service.LikeService;
import com.example.social_network_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class NotificationMapper {

    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final FollowRepository followRepository;

    public NotificationResponseDTO toDto(Notification notification) {
        if (notification == null) {
            return null;
        }

        User sender = switch (notification.getType()) {
            case LIKE -> likeRepository.findById(notification.getReferenceId())
                    .map(like -> like.getUser())
                    .orElse(null);
            case COMMENT -> commentRepository.findById(notification.getReferenceId())
                    .map(comment -> comment.getUser())
                    .orElse(null);
            case FOLLOW -> followRepository.findById(notification.getReferenceId())
                    .map(follow -> follow.getFollower())
                    .orElse(null);
        };

        return NotificationResponseDTO.builder()
                .id(notification.getId())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .type(notification.getType().toString())
                .referenceId(notification.getReferenceId())
                .senderId(sender != null ? sender.getId() : null)
                .senderUsername(sender != null ? sender.getUsername() : null)
                .message(sender != null
                        ? buildMessage(sender.getUsername(), notification.getType().toString())
                        : "Thông báo này không còn khả dụng.")
                .build();
    }

    private String buildMessage(String senderUsername, String type) {
        String text = type.equals("FOLLOW") ? " bạn." : " bài viết của bạn.";
        return senderUsername.toUpperCase() + " đã " + type.toLowerCase() + text;
    }


}
