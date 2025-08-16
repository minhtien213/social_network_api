package com.example.social_network_api.controller;

import com.example.social_network_api.dto.request.ProfileRequestDTO;
import com.example.social_network_api.dto.respone.ProfileResponseDTO;
import com.example.social_network_api.entity.Profile;
import com.example.social_network_api.mapper.ProfileMapper;
import com.example.social_network_api.repository.ProfileRepository;
import com.example.social_network_api.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class ProfileContoller {

    private final ProfileService profileService;
    private final ProfileMapper profileMapper;


    @PostMapping("/create")
    public ResponseEntity<?> createProfile(@Valid @RequestPart("profileRequestDTO") ProfileRequestDTO profileRequestDTO,
                                           @RequestPart("avatarUrl") MultipartFile avatarUrl,
                                           Principal principal) {
        try {
            Profile savedProfile = profileService.createProfile(profileRequestDTO, avatarUrl, principal.getName());
            return ResponseEntity.ok().body(profileMapper.toProfileResponseDTO(
                    savedProfile,
                    savedProfile.getUser().getFollowerCount(),
                    savedProfile.getUser().getFollowingCount())
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProfile(@PathVariable Long id,
                                           @Valid @RequestPart ProfileRequestDTO profileRequestDTO,
                                           @RequestPart MultipartFile avatarUrl,
                                           Principal principal
                                           ){
        try{
            Profile updatedProfile = profileService.updateProfile(id, profileRequestDTO, avatarUrl, principal.getName());
            return ResponseEntity.ok().body(profileMapper.toProfileResponseDTO(updatedProfile,
                    updatedProfile.getUser().getFollowerCount(),
                    updatedProfile.getUser().getFollowingCount()
                    ));
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProfile(@PathVariable Long id) {
        try{
            Profile profile = profileService.findById(id);
            return ResponseEntity.ok().body(profileMapper.toProfileResponseDTO(profile,
                    profile.getUser().getFollowerCount(), profile.getUser().getFollowingCount()));
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/list-profiles")
    public ResponseEntity<?> getAllProfiles() {
        try{
            List<Profile> profiles = profileService.findAll();
            List<ProfileResponseDTO> profilesResponse = profiles.stream()
                    .map(profile -> profileMapper.toProfileResponseDTO(
                    profile,
                    profile.getUser().getFollowerCount(),
                    profile.getUser().getFollowingCount())
                    ).collect(Collectors.toList());
            return ResponseEntity.ok().body(profilesResponse);
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProfile(@PathVariable Long id) {
        try {
            profileService.deleteById(id);
            return ResponseEntity.ok("Profile has been deleted");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
