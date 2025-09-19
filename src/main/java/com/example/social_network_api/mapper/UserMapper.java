package com.example.social_network_api.mapper;

import com.example.social_network_api.dto.request.UserRequestDTO;
import com.example.social_network_api.dto.respone.UserResponseDTO;
import com.example.social_network_api.entity.User;
import org.mapstruct.*;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "enabled", expression = "java(true)") // constant = "true"
    User toUser(UserRequestDTO userRequestDTO);

    //chỉ trả list string roles
    @Mapping(target = "roles", expression = "java(user.getRoles().stream().map(r -> r.getName()).toList())")
    UserResponseDTO toUserResponseDTO(User user);

    // Update entity từ DTO, bỏ qua các field null
    // @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    // void updateUserFromDto(UserRequestDTO dto, @MappingTarget User entity);
}
