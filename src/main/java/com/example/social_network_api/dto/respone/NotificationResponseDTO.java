package com.example.social_network_api.dto.respone;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponseDTO {
    private Long id;
    private String type;
    private Long referenceId;
    private Boolean isRead;
    private LocalDateTime createdAt;

    //người tạo
    private Long triggerUserId;
    private String triggerUsername;

    private String message; //

}
