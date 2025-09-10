package com.example.social_network_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResetPasswordDTO {
    @NotBlank(message = "newPassword không được rỗng.")
    @Size(min = 4, message = "Độ dài tối thiểu 4 ký tự.")
    private String newPassword;
    @NotBlank(message = "newPasswordConfirm không được rỗng.")
    @Size(min = 4, message = "Độ dài tối thiểu 4 ký tự.")
    private String newPasswordConfirm;
}
