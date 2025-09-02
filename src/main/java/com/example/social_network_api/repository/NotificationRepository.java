package com.example.social_network_api.repository;

import com.example.social_network_api.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,Long> {
    Page<Notification> findByReceiverId(Long receiverId, Pageable pageable);
    Long countByReceiverIdAndIsReadFalse(Long receiverId);
}
