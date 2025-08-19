package com.example.social_network_api.mapper;

import com.example.social_network_api.dto.respone.LikeResponseDTO;
import com.example.social_network_api.entity.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LikeMapper {
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "post_id", expression = "java(like.getPost().getId())")
    LikeResponseDTO toLikeResponseDTO(Like like);
}
