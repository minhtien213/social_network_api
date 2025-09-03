package com.example.social_network_api.service;

import com.example.social_network_api.entity.Notification;
import com.example.social_network_api.entity.User;
import org.springframework.data.domain.Page;

public interface NotificationService extends IService<Notification> {
    Notification createAndSentNotification(Long referenceId, User receiver, Notification.NotificationType type);
    Page<Notification> findByReceiverId(String username, int page, int size);
    Notification markAsRead(Long id, String username);
    Long countByReceiverIdAndIsReadFalse(String username);

}
