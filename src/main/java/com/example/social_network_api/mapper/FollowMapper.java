package com.example.social_network_api.mapper;

import com.example.social_network_api.dto.respone.FollowResponseDTO;
import com.example.social_network_api.entity.Follow;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FollowMapper {
    FollowResponseDTO toFollowResponseDTO(Follow follow);
}
