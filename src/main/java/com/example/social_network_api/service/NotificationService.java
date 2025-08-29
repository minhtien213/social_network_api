package com.example.social_network_api.service;

import com.example.social_network_api.entity.Notification;
import com.example.social_network_api.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface NotificationService extends IService<Notification> {
    Notification createAndSentNotification(Long referenceId, User receiver, Notification.NotificationType type);
    Page<Notification> findByReceiverId(Long receiverId, int page, int size);
}
