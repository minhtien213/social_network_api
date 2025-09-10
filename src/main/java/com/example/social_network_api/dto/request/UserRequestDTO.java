package com.example.social_network_api.dto.request;

import com.example.social_network_api.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDTO {
    @NotBlank(message = "Username không được để trống.")
    private String username;
    @NotBlank(message = "Độ dài tối thiểu 4 kí tự.")
    @Size(min = 4)
//    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$")
    private String password;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @Email(message = "Email không hợp lệ.")
    private String email;
    private List<Role> roles;
}
