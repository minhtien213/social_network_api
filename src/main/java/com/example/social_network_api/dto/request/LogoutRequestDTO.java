package com.example.social_network_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogoutRequestDTO {
    @NotBlank(message = "Access token không được để trống")
    private String accessToken;
    @NotBlank(message = "Refresh token không được để trống")
    private String refreshToken;
}
