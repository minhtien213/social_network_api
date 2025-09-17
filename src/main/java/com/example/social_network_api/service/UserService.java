package com.example.social_network_api.service;

import com.example.social_network_api.dto.request.ResetPasswordDTO;
import com.example.social_network_api.dto.request.UserRequestDTO;
import com.example.social_network_api.entity.User;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

public interface UserService extends UserDetailsService, IService<User> {

    User findByUsername(String username);
    User findByEmail(String email);

    Page<User> findByFullName(String keyword, int page, int size);

    void disableUser(Long id);
    void enableUser(Long id);

    Map<String, String> login(String username, String password);
    Map<String, String> refreshToken(String refreshToken);

    void logout(String accessToken, String refreshToken);

    User registerUser(UserRequestDTO userRequestDTO);

    void sentPasswordResetToken(@Valid @RequestBody String username);

    void resetPassword(String token, @Valid @RequestBody ResetPasswordDTO resetPasswordDTO);

    User updateUser(Long id, UserRequestDTO userRequestDTO);

}
