package com.example.social_network_api.service;

import com.example.social_network_api.dto.request.UserRequestDTO;
import com.example.social_network_api.entity.User;
import jakarta.validation.Valid;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestBody;

public interface UserService extends UserDetailsService, IService<User> {
    User findByUsername(String username);

    User findByEmail(String email);

    void disableUser(Long id);

    void enableUser(Long id);

    User registerUser(UserRequestDTO userRequestDTO);

    User updateUser(Long id, UserRequestDTO userRequestDTO);

}
