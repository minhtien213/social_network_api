package com.example.social_network_api.dto.respone;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponseDTO {

    private Long id;
    private Long senderId;
    private Long receiverId;
    private Long groupId;
    private String content;
    private boolean isRead;
    private LocalDateTime createdAt;
}
