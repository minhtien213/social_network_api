package com.example.social_network_api.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileRequestDTO {

//    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 50, message = "Họ tên tối đa 50 ký tự")
    private String fullName;

    @Size(max = 300, message = "Bio tối đa 300 ký tự")
    private String bio;

    @URL(message = "avatarUrl phải là đường dẫn hợp lệ")
    private String avatarUrl;

    @PastOrPresent(message = "Ngày sinh không hợp lệ")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate birthday;

    private Boolean gender;

    @Size(max = 100, message = "Địa điểm tối đa 100 ký tự")
    private String location;

    @Pattern(
            regexp = "^(0|\\+84)(3[2-9]|5[2689]|7[0-9]|8[1-9]|9[0-9])[0-9]{7}$",
            message = "Số điện thoại không hợp lệ"
    )
    private String phone;
}
