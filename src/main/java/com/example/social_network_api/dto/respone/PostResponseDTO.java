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
    private Long id;
    private String content;
    private String username;
    private List<String> postMediaList;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
