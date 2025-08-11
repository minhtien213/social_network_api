package com.example.social_network_api.mapper;

import com.example.social_network_api.dto.request.UserDTO;
import com.example.social_network_api.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface UserMapper {
    public User toUser(UserDTO userDTO);

    public UserDTO toUserDTO(User user);

    //update ằng dto không thay đổi role
    @Mapping(target = "roles", ignore = true)
    void updateUserFromUserDTO(UserDTO userDTO, @MappingTarget User user);

    //update bằng entity không cần set thủ công từng field
    void updateEntityFromEntity(User source, @MappingTarget User target);
}
