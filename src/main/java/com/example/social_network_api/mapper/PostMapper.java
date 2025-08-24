package com.example.social_network_api.mapper;

import com.example.social_network_api.dto.request.PostRequestDTO;
import com.example.social_network_api.dto.respone.PostResponseDTO;
import com.example.social_network_api.entity.Post;
import com.example.social_network_api.entity.PostMedia;
import com.example.social_network_api.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

//unmappedTargetPolicy tự động map các field
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    //mapstruct không tự map được các field nếu không trùng tên + kdl
    //dùng mapping để chỉ định
    //target là field muốn map vào
    //source là dữ liệu sẽ map vào target

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "postMediaList", ignore = true)
    Post toPost(PostRequestDTO postRequestDTO, User user);


    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "postMediaList", expression = "java(mapMediaUrls(post))")
    @Mapping(target = "likesCount", expression = "java(post.getLikes().size())")
    @Mapping(target = "commentsCount", expression = "java(post.getComments().size())")
    PostResponseDTO toPostResponseDTO(Post post);

    //object -> list string -> trả về client
    default List<String> mapMediaUrls(Post post){
        return post.getPostMediaList()
                .stream()
                .map(media -> media.getMediaUrl())
                .collect(Collectors.toList());
    };

}
