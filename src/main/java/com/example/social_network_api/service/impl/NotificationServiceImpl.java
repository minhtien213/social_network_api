package com.example.social_network_api.service.impl;

import com.example.social_network_api.dto.respone.NotificationResponseDTO;
import com.example.social_network_api.entity.Notification;
import com.example.social_network_api.entity.User;
import com.example.social_network_api.exception.custom.ForbiddenException;
import com.example.social_network_api.mapper.NotificationMapper;
import com.example.social_network_api.repository.CommentRepository;
import com.example.social_network_api.repository.FollowRepository;
import com.example.social_network_api.repository.LikeRepository;
import com.example.social_network_api.repository.NotificationRepository;
import com.example.social_network_api.service.NotificationService;
import com.example.social_network_api.service.UserService;
import com.example.social_network_api.utils.AuthUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final LikeRepository likeRepository;
    private final FollowRepository followRepository;
    private final CommentRepository commentRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;
    private final NotificationMapper notificationMapper;

    public Notification createAndSentNotification(Long referenceId, User receiver, Notification.NotificationType type) {
        Notification notification = Notification.builder()
                .referenceId(referenceId)
                .receiver(receiver)
                .type(type)
                .isRead(false)
                .build();
        Notification saved = notificationRepository.save(notification);

        User sender = switch (type) {
            case LIKE -> likeRepository.findById(referenceId).get().getUser();
            case COMMENT -> commentRepository.findById(referenceId).get().getUser();
            case FOLLOW -> followRepository.findById(referenceId).get().getFollower();
        };

        System.out.println("Send notification to userId: " + receiver.getId());

        // Push notification riêng cho user
        messagingTemplate.convertAndSendToUser(
                receiver.getId().toString(),               // userId dạng String
                "/queue/notifications",                    // kênh riêng user
                notificationMapper.toDto(saved, sender)   // payload
        );

        return saved;
    }


    @Override
    public Page<Notification> findByReceiverId(Long receiverId, int page, int size) {
        User receiver = userService.findById(receiverId);
        if(!receiver.getUsername().equals(AuthUtils.getCurrentUsername())) {
            throw new ForbiddenException("User is not allowed to read notifications");
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return notificationRepository.findByReceiverId(receiverId, pageable);
    }

    @Override
    public void deleteById(Long id) {
        if(notificationRepository.existsById(id)){
            notificationRepository.deleteById(id);
        }
    }

    @Override
    public Page<Notification> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return  notificationRepository.findAll(pageable);
    }

    @Override
    public Notification findById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException("Notification not found"));
    }
}
