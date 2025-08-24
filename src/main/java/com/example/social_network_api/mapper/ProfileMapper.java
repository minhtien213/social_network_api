package com.example.social_network_api.mapper;

import com.example.social_network_api.dto.request.ProfileRequestDTO;
import com.example.social_network_api.dto.respone.ProfileResponseDTO;
import com.example.social_network_api.entity.Profile;
import com.example.social_network_api.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProfileMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    Profile toProfile(ProfileRequestDTO profileRequestDTO, User user);

    @Mapping(target = "followerCount", source = "followerCount")
    @Mapping(target = "followingCount", source = "followingCount")
    ProfileResponseDTO toProfileResponseDTO(Profile profile, int followerCount, int followingCount);
}
