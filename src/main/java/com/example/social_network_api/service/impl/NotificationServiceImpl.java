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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

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
    public List<Notification> findByReceiverId(Long receiverId) {
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        if(!receiver.getUsername().equals(AuthUtils.getCurrentUsername())) {
            throw new ForbiddenException("User is not allowed to read notifications");
        }
        return notificationRepository.findByReceiverId(receiverId);
    }

    @Override
    public void deleteById(Long id) {
        if(notificationRepository.existsById(id)){
            notificationRepository.deleteById(id);
        }
    }

    @Override
    public List<Notification> findAll() {
        return  notificationRepository.findAll();
    }

    @Override
    public Notification findById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException("Notification not found"));
    }
}
