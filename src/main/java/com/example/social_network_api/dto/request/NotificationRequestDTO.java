package com.example.social_network_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequestDTO {
    @NotNull(message = "receiverId không được null")
    @Positive(message = "receiverId phải lớn hơn 0")
    private Long receiverId;

    @NotNull(message = "referenceId không được null")
    @Positive(message = "referenceId phải lớn hơn 0")
    private Long referenceId;

    @NotBlank(message = "type không được để trống")
    private String type;
}
