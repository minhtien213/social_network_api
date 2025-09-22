package com.example.social_network_api.dto.respone;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResponseDTO {
    private Long id;
    private String fullName;
    private String bio;
    private String avatarUrl;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate birthday;
    private boolean gender;
    private String location;
    private String phone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int followerCount;
    private int followingCount;
}
