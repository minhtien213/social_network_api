package com.example.social_network_api.mapper;

import com.example.social_network_api.dto.request.CommentRequestDTO;
import com.example.social_network_api.dto.respone.CommentResponseDTO;
import com.example.social_network_api.entity.Comment;
import com.example.social_network_api.entity.Post;
import com.example.social_network_api.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "post", source = "post")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "content", source = "commentRequestDTO.content")
    @Mapping(target = "mediaUrl", source = "mediaUrl")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    Comment toComment(Post post, User user, CommentRequestDTO commentRequestDTO, String mediaUrl);

    @Mapping(target = "username", expression = "java(getUsername(comment))")
    @Mapping(target = "post_id", expression = "java(comment.getPost().getId())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    CommentResponseDTO toCommentResponseDTO(Comment comment);

    default String getUsername(Comment comment) {
        return comment.getUser() != null ? comment.getUser().getUsername() : null;
    }

}
