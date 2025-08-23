package com.example.social_network_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResetPasswordDTO {
    @NotBlank(message = "Không được rỗng.")
    private String newPassword;
    @NotBlank(message = "Không được rỗng.")
    private String newPasswordConfirm;
}
