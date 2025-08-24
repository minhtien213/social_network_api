package com.example.social_network_api.service.impl;

import com.example.social_network_api.entity.Role;
import com.example.social_network_api.repository.RoleRepository;
import com.example.social_network_api.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Role findByName(String name) {
        return roleRepository.findByName(name);
    }
}
