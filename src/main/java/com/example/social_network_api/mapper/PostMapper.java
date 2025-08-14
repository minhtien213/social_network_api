package com.example.social_network_api.mapper;

import com.example.social_network_api.dto.request.PostRequestDTO;
import com.example.social_network_api.dto.respone.PostResponseDTO;
import com.example.social_network_api.entity.Post;
import com.example.social_network_api.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "mediaUrl", source = "mediaUrl")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    Post toPost(PostRequestDTO postRequestDTO, User user, String mediaUrl);


    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "mediaUrl", source = "mediaUrl")
    PostResponseDTO toPostResponseDTO(Post post);

}
