package com.example.social_network_api.service.impl;

import com.example.social_network_api.config.UploadsUtils;
import com.example.social_network_api.dto.request.ProfileRequestDTO;
import com.example.social_network_api.entity.Profile;
import com.example.social_network_api.entity.User;
import com.example.social_network_api.exception.custom.ResourceNotfoundException;
import com.example.social_network_api.mapper.ProfileMapper;
import com.example.social_network_api.repository.ProfileRepository;
import com.example.social_network_api.repository.UserRepository;
import com.example.social_network_api.service.ProfileService;
import com.example.social_network_api.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Profile save(Profile profile) {
        return profileRepository.save(profile);
    }

    @Override
    @Transactional
    public Profile update(Long id, Profile profile) {
        return null;
    }

    @Transactional
    public Profile createProfile(ProfileRequestDTO profileRequestDTO, MultipartFile avatarUrl, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotfoundException("User not found"));

        if(avatarUrl != null) {
            String avatarPath = UploadsUtils.uploadFile(avatarUrl);
            profileRequestDTO.setAvatarUrl(avatarPath);
        }

        Profile profileSaved = profileRepository.save(profileMapper.toProfile(profileRequestDTO, user));
        return profileSaved;
    }

    @Transactional
    public Profile updateProfile(Long id, ProfileRequestDTO profileRequestDTO, MultipartFile avatarUrl, String username) {
        Profile existingProfile = profileRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Profile not found")
        );
        if(!existingProfile.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized attempt to update profile");
        }

        if(avatarUrl != null) {
            String avatarPath = UploadsUtils.uploadFile(avatarUrl);
            existingProfile.setAvatarUrl(avatarPath);
        }

        existingProfile.setUpdatedAt(LocalDateTime.now());
        existingProfile.setFullName(profileRequestDTO.getFullName());
        existingProfile.setBio(profileRequestDTO.getBio());
        existingProfile.setGender(profileRequestDTO.isGender());
        existingProfile.setLocation(profileRequestDTO.getLocation());

        Profile profileSaved = profileRepository.save(existingProfile);
        return profileSaved;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Profile profile = profileRepository.findById(id).orElseThrow(
                () -> new ResourceNotfoundException("Profile with id " + id + " not found")
        );
        profileRepository.deleteById(id);
    }

    @Override
    public List<Profile> findAll() {
        List<Profile> profiles = profileRepository.findAll();
        if(profiles.isEmpty()) {
            throw new ResourceNotfoundException("No profiles found");
        }
        return profiles;
    }

    @Override
    public Profile findById(Long id) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotfoundException("Profile with id " + id + " not found")
        );
        return profile;
    }
}
