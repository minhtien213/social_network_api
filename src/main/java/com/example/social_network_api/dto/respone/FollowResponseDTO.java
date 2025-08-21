package com.example.social_network_api.dto.respone;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowResponseDTO {
    private Long followId;
    private Long followerId;
    private Long followingId;
    private String followStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

