package com.example.social_network_api.service.impl;

import com.example.social_network_api.dto.request.MessageRequestDTO;
import com.example.social_network_api.entity.Message;
import com.example.social_network_api.entity.User;
import com.example.social_network_api.exception.custom.ResourceNotFoundException;
import com.example.social_network_api.mapper.MessageMapper;
import com.example.social_network_api.repository.MessageRepository;
import com.example.social_network_api.repository.UserRepository;
import com.example.social_network_api.service.MessageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void saveAndSendMessage(MessageRequestDTO messageRequestDTO, String senderUsername) {

        User sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));

        Message message = messageMapper.toMessage(messageRequestDTO);
        message.setSenderId(sender.getId()); // đảm bảo chính chủ

        User receiver = userRepository.findById(message.getReceiverId())
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found"));

        System.out.println(receiver.getUsername());

        Message savedMessage = messageRepository.save(message);
        messagingTemplate.convertAndSendToUser(
                receiver.getUsername(),
                "/queue/messages",
                messageMapper.toMessageResponseDTO(savedMessage)
        );
    }

    @Override
    public Page<Message> findBySenderIdAndReceiverId(Long senderId, Long receiverId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        return messageRepository.findBySenderIdAndReceiverId(senderId, receiverId, pageable);
    }

    @Override
    public void deleteById(Long id) {
        messageRepository.deleteById(id);
    }

    @Override
    public Page<Message> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        return messageRepository.findAll(pageable);
    }

    @Override
    public Message findById(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found."));
        return message;
    }

}
