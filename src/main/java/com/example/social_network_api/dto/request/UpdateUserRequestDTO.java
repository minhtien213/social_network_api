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
public class UpdateUserRequestDTO {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    @Email
    private String email;
}
