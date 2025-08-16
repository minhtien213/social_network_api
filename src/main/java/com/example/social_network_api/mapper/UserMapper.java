package com.example.social_network_api.mapper;

import com.example.social_network_api.dto.request.UserRequestDTO;
import com.example.social_network_api.dto.respone.UserResponseDTO;
import com.example.social_network_api.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "enabled", expression = "java(true)") // constant = "true"
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    public User toUser(UserRequestDTO userRequestDTO);

    //chỉ trả list string roles
    @Mapping(target = "roles", expression = "java(user.getRoles().stream().map(r -> r.getName()).toList())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    public UserResponseDTO toUserResponseDTO(User user);
}
