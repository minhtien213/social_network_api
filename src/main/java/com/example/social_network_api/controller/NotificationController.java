package com.example.social_network_api.controller;

import com.example.social_network_api.dto.request.NotificationRequestDTO;
import com.example.social_network_api.dto.respone.NotificationResponseDTO;
import com.example.social_network_api.entity.Notification;
import com.example.social_network_api.mapper.NotificationMapper;
import com.example.social_network_api.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
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
        Page<NotificationResponseDTO> dtos = notifications.map(dto -> notificationMapper.toDto(dto, null));
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}/mark-as-read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id, Principal principal){
        Notification notification = notificationService.markAsRead(id, principal.getName());
        return ResponseEntity.ok("The notice has been viewed");
    }

    @GetMapping("/notification-counts")
    public ResponseEntity<?> getCountUnRead(Principal principal){
        Long count = notificationService.countByReceiverIdAndIsReadFalse(principal.getName());
        return ResponseEntity.ok(Map.of("unRead_count", count));
    }

}

