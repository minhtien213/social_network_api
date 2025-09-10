package com.example.social_network_api.service;


import com.example.social_network_api.dto.request.MessageRequestDTO;
import com.example.social_network_api.entity.Message;
import org.springframework.data.domain.Page;

public interface MessageService extends IService<Message> {
    Page<Message> findBySenderIdAndReceiverId(Long senderId, Long receiverId, int page, int size);

    void saveAndSendMessage(MessageRequestDTO messageRequestDTO, String senderUsername);

}
