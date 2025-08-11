package com.example.social_network_api.service;

import com.example.social_network_api.entity.Role;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleService {
    public Role findByName(String name);
}

