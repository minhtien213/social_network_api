package com.example.social_network_api.mapper;

import com.example.social_network_api.dto.request.CommentRequestDTO;
import com.example.social_network_api.dto.respone.CommentResponseDTO;
import com.example.social_network_api.entity.Comment;
import com.example.social_network_api.entity.Post;
import com.example.social_network_api.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "post", source = "post")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "content", source = "commentRequestDTO.content")
    @Mapping(target = "mediaUrl", source = "mediaUrl")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    Comment toComment(Post post, User user, CommentRequestDTO commentRequestDTO, String mediaUrl);

    @Mapping(target = "username", source = "user.username")   // user lấy từ Comment.user
    @Mapping(target = "post_id", source = "post.id")
    CommentResponseDTO toCommentResponseDTO(Comment comment);

    default String getUsername(Comment comment) {
        return comment.getUser() != null ? comment.getUser().getUsername() : null;
    }

}
