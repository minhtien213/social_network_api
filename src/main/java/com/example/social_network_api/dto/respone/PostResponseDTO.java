package com.example.social_network_api.dto.respone;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponseDTO {
    public String content;
    public String username;
    public String mediaUrl;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
}
