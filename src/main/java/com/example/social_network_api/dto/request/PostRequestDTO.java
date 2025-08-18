package com.example.social_network_api.dto.request;

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
}
