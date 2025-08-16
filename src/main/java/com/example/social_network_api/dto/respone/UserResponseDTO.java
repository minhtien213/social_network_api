package com.example.social_network_api.dto.respone;

import com.example.social_network_api.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private List<String> roles; // trả về tên role thay vì entity đầy đủ
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
