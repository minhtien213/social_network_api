package com.example.social_network_api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowRequestDTO {
    @NotNull(message = "followerId không được null")
    private Long followerId;
    @NotNull(message = "followingId không được null")
    private Long followingId;
}
