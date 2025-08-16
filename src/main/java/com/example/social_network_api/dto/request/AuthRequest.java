package com.example.social_network_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthRequest {
    @NotBlank(message = "Username không được để trống!")
    private String username;
    @NotBlank(message = "Password không được để trống!")
    @Size(min = 8)
    private String password;
}
