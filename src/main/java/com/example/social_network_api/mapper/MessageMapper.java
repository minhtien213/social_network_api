package com.example.social_network_api.mapper;

import com.example.social_network_api.dto.request.MessageRequestDTO;
import com.example.social_network_api.dto.respone.MessageResponseDTO;
import com.example.social_network_api.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MessageMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isRead", constant = "false")
    Message toMessage(MessageRequestDTO messageRequestDTO);

    MessageResponseDTO toMessageResponseDTO(Message message);
}
