package com.example.social_network_api.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageRequestDTO {
    @NonNull
    private Long senderId;
    @NonNull
    private Long receiverId;
    @NonNull
    private String content;
}
