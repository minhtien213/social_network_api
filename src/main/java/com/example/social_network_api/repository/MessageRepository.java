package com.example.social_network_api.repository;

import com.example.social_network_api.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    Page<Message> findBySenderIdAndReceiverId(Long senderId, Long receiverId, Pageable pageable);
}
