package com.example.social_network_api.dto.request;

import com.example.social_network_api.entity.User;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequestDTO {
    private Long receiverId;
    private Long referenceId;
    private String type;
}
