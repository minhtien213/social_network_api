package com.example.social_network_api.service.impl;

import com.example.social_network_api.entity.Notification;
import com.example.social_network_api.entity.User;
import com.example.social_network_api.exception.custom.ForbiddenException;
import com.example.social_network_api.repository.NotificationRepository;
import com.example.social_network_api.repository.UserRepository;
import com.example.social_network_api.service.NotificationService;
import com.example.social_network_api.service.UserService;
import com.example.social_network_api.utils.AuthUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;

    public Notification createAndSentNotification(Long referenceId, User receiver, Notification.NotificationType type) {
        Notification notification = Notification.builder()
                .referenceId(referenceId)
                .receiver(receiver)
                .type(type)
                .isRead(false)
                .build();
        return notificationRepository.save(notification);
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
