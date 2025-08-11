package com.example.social_network_api.service;

import com.example.social_network_api.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService, IService<User> {
    public User findByUsername(String username);
    public User findByEmail(String email);
    public void disableUser(Long id);
    public void enableUser(Long id);
}
