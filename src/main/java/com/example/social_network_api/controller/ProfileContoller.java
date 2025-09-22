package com.example.social_network_api.controller;

import com.example.social_network_api.dto.request.ProfileRequestDTO;
import com.example.social_network_api.dto.respone.ProfileResponseDTO;
import com.example.social_network_api.entity.Profile;
import com.example.social_network_api.mapper.ProfileMapper;
import com.example.social_network_api.repository.ProfileRepository;
import com.example.social_network_api.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
                                           @RequestPart(value = "avatarUrl", required = false) MultipartFile avatarUrl,
                                           Principal principal) {
        Profile savedProfile = profileService.createProfile(profileRequestDTO, avatarUrl, principal.getName());
        return ResponseEntity.ok().body(profileMapper.toProfileResponseDTO(savedProfile));
    }

    @PutMapping("/userId/{userId}")
    public ResponseEntity<?> updateProfile(@PathVariable Long userId,
                                           @Valid @RequestPart ProfileRequestDTO profileRequestDTO,
                                           @RequestPart(value = "avatarUrl", required = false) MultipartFile avatarUrl
    ) {
        Profile updatedProfile = profileService.updateProfile(userId, profileRequestDTO, avatarUrl);
        return ResponseEntity.ok().body(profileMapper.toProfileResponseDTO(updatedProfile));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProfile(@PathVariable Long id) {
        Profile profile = profileService.findById(id);
        return ResponseEntity.ok().body(profileMapper.toProfileResponseDTO(profile));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<?>> getProfileByFullName(@RequestParam String keyword, int page, int size) {

        Page<Profile> profiles = profileService.findByFullName(keyword, page, size);

        Page<ProfileResponseDTO> dtos = profiles
                .map(profile -> profileMapper.toProfileResponseDTO(profile));

        return ResponseEntity.ok().body(dtos);
    }

    @GetMapping("/list-profiles")
    public ResponseEntity<Page<?>> getAllProfiles(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "3") int size) {
        Page<Profile> profiles = profileService.findAll(page, size);
        Page<ProfileResponseDTO> profilesResponse = profiles.map(profile -> profileMapper.toProfileResponseDTO(profile));
        return ResponseEntity.ok().body(profilesResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProfile(@PathVariable Long id) {
        profileService.deleteById(id);
        return ResponseEntity.ok("Profile has been deleted");
    }
}
