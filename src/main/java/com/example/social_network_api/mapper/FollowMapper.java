package com.example.social_network_api.mapper;

import com.example.social_network_api.dto.respone.FollowResponseDTO;
import com.example.social_network_api.entity.Follow;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FollowMapper {
    @Mapping(target = "followId", source = "id")
    @Mapping(target = "followerId", source = "follower.id")
    @Mapping(target = "followingId", source = "following.id")
    FollowResponseDTO toFollowResponseDTO(Follow follow);
}
