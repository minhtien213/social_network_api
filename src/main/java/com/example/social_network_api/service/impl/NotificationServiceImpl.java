package com.example.social_network_api.service.impl;

import com.example.social_network_api.dto.respone.NotificationResponseDTO;
import com.example.social_network_api.entity.Notification;
import com.example.social_network_api.entity.User;
import com.example.social_network_api.exception.custom.ForbiddenException;
import com.example.social_network_api.exception.custom.ResourceNotFoundException;
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

        messagingTemplate.convertAndSendToUser(
                receiver.getUsername(),
                "/queue/notifications",
                notificationMapper.toDto(saved)
        );

        return saved;
    }


    @Override
    public Page<Notification> findByReceiverId(String username, int page, int size) {
        User receiver = userService.findByUsername(username);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return notificationRepository.findByReceiverId(receiver.getId(), pageable);
    }

    @Override
    public Long countByReceiverIdAndIsReadFalse(String username) {
        User currentUser = userService.findByUsername(username);
        return notificationRepository.countByReceiverIdAndIsReadFalse(currentUser.getId());
    }

    @Override
    public Notification markAsRead(Long id, String username) {

        Notification existingNotification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found."));

        User receiver = userService.findById(existingNotification.getReceiver().getId());
        if(!username.equals(receiver.getUsername())){
            throw new ForbiddenException("Unauthorized.");
        }

        existingNotification.setRead(true);
        return notificationRepository.save(existingNotification);
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
