package com.example.social_network_api.controller;

import com.example.social_network_api.dto.respone.NotificationResponseDTO;
import com.example.social_network_api.entity.Notification;
import com.example.social_network_api.mapper.NotificationMapper;
import com.example.social_network_api.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    @GetMapping("/{receiverId}")
    public ResponseEntity<Page<?>> findByReceiverId(@PathVariable Long receiverId,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "3") int size
                                                    ) {
        Page<Notification> notifications = notificationService.findByReceiverId(receiverId, page, size);
        Page<NotificationResponseDTO> dtos = notifications.map(dto -> notificationMapper.toDto(dto));
        return ResponseEntity.ok(dtos);
    }
}
