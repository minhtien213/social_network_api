package com.example.social_network_api.dto.respone;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponseDTO {
    public String content;
    public String username;
    public List<String> postMediaList;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
}
