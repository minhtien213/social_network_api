package com.example.social_network_api.mapper;

import com.example.social_network_api.dto.request.UserRequestDTO;
import com.example.social_network_api.dto.respone.UserResponseDTO;
import com.example.social_network_api.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "enabled", expression = "java(true)") // constant = "true"
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    User toUser(UserRequestDTO userRequestDTO);

    //chỉ trả list string roles
    @Mapping(target = "roles", expression = "java(user.getRoles().stream().map(r -> r.getName()).toList())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    UserResponseDTO toUserResponseDTO(User user);
}
