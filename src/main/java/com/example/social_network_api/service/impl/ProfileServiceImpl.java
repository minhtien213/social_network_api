package com.example.social_network_api.service.impl;

import com.example.social_network_api.exception.custom.ForbiddenException;
import com.example.social_network_api.utils.AuthUtils;
import com.example.social_network_api.utils.UploadsUtils;
import com.example.social_network_api.dto.request.ProfileRequestDTO;
import com.example.social_network_api.entity.Profile;
import com.example.social_network_api.entity.User;
import com.example.social_network_api.exception.custom.ResourceNotFoundException;
import com.example.social_network_api.mapper.ProfileMapper;
import com.example.social_network_api.repository.ProfileRepository;
import com.example.social_network_api.repository.UserRepository;
import com.example.social_network_api.service.ProfileService;
import com.example.social_network_api.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;
    private final UserRepository userRepository;
    private final UserService userService;

    @Transactional
    public Profile createProfile(ProfileRequestDTO profileRequestDTO, MultipartFile avatarUrl, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if(avatarUrl != null) {
            String avatarPath = UploadsUtils.uploadFile(avatarUrl);
            profileRequestDTO.setAvatarUrl(avatarPath);
        }

        Profile profileSaved = profileRepository.save(profileMapper.toProfile(profileRequestDTO, user));
        return profileSaved;
    }

    @Transactional
    public Profile updateProfile(ProfileRequestDTO profileRequestDTO, MultipartFile avatarUrl, String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }

        Profile existingProfile = profileRepository.findByUserId(user.getId()).orElseThrow(
                () -> new RuntimeException("Profile not found")
        );

        if(!existingProfile.getUser().getUsername().equals(username) && !AuthUtils.isAdmin()) {
            throw new RuntimeException("Unauthorized attempt to update profile");
        }

        if(avatarUrl != null) {
            String avatarPath = UploadsUtils.uploadFile(avatarUrl);
            existingProfile.setAvatarUrl(avatarPath);
        }

        existingProfile.setUpdatedAt(LocalDateTime.now());
        existingProfile.setFullName(profileRequestDTO.getFullName());
        existingProfile.setBio(profileRequestDTO.getBio());
        existingProfile.setBirthday(profileRequestDTO.getBirthday());
        existingProfile.setGender(profileRequestDTO.isGender());
        existingProfile.setLocation(profileRequestDTO.getLocation());
        existingProfile.setPhone(profileRequestDTO.getPhone());

        Profile profileSaved = profileRepository.save(existingProfile);
        return profileSaved;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Profile profile = profileRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Profile with id " + id + " not found")
        );
        profileRepository.deleteById(id);
    }

    @Override
    public Page<Profile> findAll(int page, int size) {
        if(!AuthUtils.isAdmin()){
            throw new ForbiddenException("Unauthorized");
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return profileRepository.findAll(pageable);
    }

    @Override
    public Profile findById(Long id) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile with id " + id + " not found")
        );
        return profile;
    }

    @Override
    public Page<Profile> findByFullName(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Profile> profiles = profileRepository.findByFullName(keyword, pageable);
        return profiles;
    }
}
