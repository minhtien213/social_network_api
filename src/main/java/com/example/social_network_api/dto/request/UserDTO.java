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
//tạo các validation khi register user
public class UserDTO {

    @NotBlank(message = "Khong duoc de trong!")
    private String username;
    @NotBlank(message = "Do dai toi thieu 8 ki tu!")
    @Size(min = 4, max = 8)
//    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$")
    private String password;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @Email(message = "Email khong hop le!")
    private String email;
    private List<Role> roles;
}
