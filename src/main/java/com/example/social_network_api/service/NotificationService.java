package com.example.social_network_api.service;

import com.example.social_network_api.entity.Notification;
import com.example.social_network_api.entity.User;

import java.util.List;

public interface NotificationService extends IService<Notification> {
    Notification createAndSentNotification(Long referenceId, User receiver, Notification.NotificationType type);
    List<Notification> findByReceiverId(Long receiverId);
}
