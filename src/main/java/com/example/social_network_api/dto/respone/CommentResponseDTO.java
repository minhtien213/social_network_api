package com.example.social_network_api.dto.respone;

import com.example.social_network_api.entity.Post;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponseDTO {
    private String content;
    private String mediaUrl;
    private String username;
    private Long post_id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
