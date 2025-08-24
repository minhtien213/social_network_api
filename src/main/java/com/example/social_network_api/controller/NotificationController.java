package com.example.social_network_api.controller;

import com.example.social_network_api.dto.respone.NotificationResponseDTO;
import com.example.social_network_api.entity.Notification;
import com.example.social_network_api.mapper.NotificationMapper;
import com.example.social_network_api.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    @GetMapping("/{receiverId}")
    public ResponseEntity<List<?>> findByReceiverId(@PathVariable Long receiverId) {
        List<Notification> notifications = notificationService.findByReceiverId(receiverId);
        List<NotificationResponseDTO> dtos = notifications.stream()
                .map(dto -> notificationMapper.toDto(dto))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
