package com.example.social_network_api.dto.respone;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeResponseDTO {
    private Long id;
    private String username;
    private Long post_id;
    private LocalDateTime createdAt;
}
