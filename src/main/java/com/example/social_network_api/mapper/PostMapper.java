package com.example.social_network_api.mapper;

import com.example.social_network_api.dto.request.PostRequestDTO;
import com.example.social_network_api.dto.respone.PostResponseDTO;
import com.example.social_network_api.entity.Post;
import com.example.social_network_api.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {

    //mapstruct không tự map được các field nếu không trùng tên + kdl
    //dùng mapping để chỉ định
    //target là field muốn map vào
    //source là dữ liệu sẽ map vào target

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "mediaUrls", source = "mediaPathString")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    Post toPost(PostRequestDTO postRequestDTO, User user, String mediaPathString);


    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "mediaUrls", source = "mediaUrls")
    PostResponseDTO toPostResponseDTO(Post post);

}
