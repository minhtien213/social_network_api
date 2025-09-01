package com.example.social_network_api.mapper;

import com.example.social_network_api.dto.respone.NotificationResponseDTO;
import com.example.social_network_api.entity.Notification;
import com.example.social_network_api.entity.User;
import com.example.social_network_api.service.CommentService;
import com.example.social_network_api.service.FollowService;
import com.example.social_network_api.service.LikeService;
import com.example.social_network_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class NotificationMapper {

    public NotificationResponseDTO toDto(Notification notification, User sender) {
        if (notification == null) {
            return null;
        }
        NotificationResponseDTO dto = new NotificationResponseDTO();
        dto.setId(notification.getId());
        dto.setIsRead(notification.isRead());
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setType(notification.getType().toString());
        dto.setReferenceId(notification.getReferenceId());
        dto.setSenderId(sender.getId());
        dto.setSenderUsername(sender.getUsername());
        dto.setMessage(buildMessage(dto));
        return dto;
    }

    public String buildMessage(NotificationResponseDTO dto){
        String text = "";
        if(dto.getType().equals("FOLLOW")){
            text = " bạn.";
        }else{
            text = " bài viết của bạn.";
        }
        String message = dto.getSenderUsername().toUpperCase() + " đã " + dto.getType().toLowerCase() + text;
        return message;
    }

}
