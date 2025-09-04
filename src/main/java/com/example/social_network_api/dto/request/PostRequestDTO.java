package com.example.social_network_api.dto.request;

import jakarta.validation.constraints.AssertTrue;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostRequestDTO {
    public  String content;
    private List<MultipartFile> files; // chứa các file client gởi lên

    @AssertTrue(message = "Nội dung hoặc media phải có ít nhất 1")
    public boolean isValidPost() {
        boolean hasContent = content != null && !content.trim().isEmpty();
        boolean hasFiles = files != null && !files.isEmpty();
        return hasContent || hasFiles;
    }

}
