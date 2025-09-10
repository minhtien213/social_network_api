package com.example.social_network_api.controller;

import com.example.social_network_api.dto.request.MessageRequestDTO;
import com.example.social_network_api.entity.User;
import com.example.social_network_api.exception.custom.ResourceNotFoundException;
import com.example.social_network_api.repository.UserRepository;
import com.example.social_network_api.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final UserRepository userRepository;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Valid @Payload MessageRequestDTO messageRequestDTO,
                            Principal principal){
        messageService.saveAndSendMessage(messageRequestDTO, principal.getName());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUsername(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Map<String, String> response = new HashMap<>();
        response.put("username", user.getUsername());
        return ResponseEntity.ok(response);
    }


}
