package com.example.social_network_api.dto.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentRequestDTO {
    private String content;
    private MultipartFile mediaUrl;
}
