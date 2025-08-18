package com.example.social_network_api.service;

import com.example.social_network_api.dto.request.ProfileRequestDTO;
import com.example.social_network_api.entity.Profile;
import org.springframework.web.multipart.MultipartFile;

public interface ProfileService extends IService<Profile> {
    public  Profile createProfile(ProfileRequestDTO profileRequestDTO, MultipartFile avatarUrl, String username);
    public  Profile updateProfile(ProfileRequestDTO profileRequestDTO,MultipartFile avatarUrl, String username);
}
