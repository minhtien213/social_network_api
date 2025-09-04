package com.example.social_network_api.dto.request;

import jakarta.validation.constraints.AssertTrue;
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


    @AssertTrue(message = "Nội dung hoặc media phải có ít nhất 1")
    public boolean isValidComment() {
        return (content != null && !content.trim().isEmpty())
                || (mediaUrl != null && !mediaUrl.isEmpty());
    }
}
