package com.example.social_network_api.mapper;

import com.example.social_network_api.dto.request.ProfileRequestDTO;
import com.example.social_network_api.dto.respone.ProfileResponseDTO;
import com.example.social_network_api.entity.Profile;
import com.example.social_network_api.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    Profile toProfile(ProfileRequestDTO profileRequestDTO, User user);

    @Mapping(target = "followerCount", source = "followerCount")
    @Mapping(target = "followingCount", source = "followingCount")
    ProfileResponseDTO toProfileResponseDTO(Profile profile, int followerCount, int followingCount);
}
