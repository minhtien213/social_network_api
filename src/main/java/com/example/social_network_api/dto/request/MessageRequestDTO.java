package com.example.social_network_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageRequestDTO {
    @NotNull(message = "senderId không được null")
    private Long senderId;
    @NotNull(message = "receiverId không được null")
    private Long receiverId;

    @NotBlank(message = "Nội dung tin nhắn không được để trống")
    @Size(max = 500, message = "Nội dung tin nhắn tối đa 500 ký tự")
    private String content;
}
