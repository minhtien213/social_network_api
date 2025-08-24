package com.example.social_network_api.mapper;

import com.example.social_network_api.dto.respone.NotificationResponseDTO;
import com.example.social_network_api.entity.Notification;
import com.example.social_network_api.entity.User;
import com.example.social_network_api.service.CommentService;
import com.example.social_network_api.service.FollowService;
import com.example.social_network_api.service.LikeService;
import com.example.social_network_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class NotificationMapper {

    private final UserService userService;
    private final FollowService followService;
    private final LikeService likeService;
    private final CommentService commentService;

    public NotificationResponseDTO toDto(Notification notification) {
        if (notification == null) {
            return null;
        }
        NotificationResponseDTO dto = new NotificationResponseDTO();
        dto.setId(notification.getId());
        dto.setIsRead(notification.isRead());
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setType(notification.getType().toString());
        dto.setReferenceId(notification.getReferenceId());
        dto.setTriggerUserId(getSender(notification).getId());
        dto.setTriggerUsername(getSender(notification).getUsername());
        dto.setMessage(buildMessage(dto));
        return dto;
    }


    public User getSender(Notification notification) {
        Long id = notification.getReferenceId();
        User sender = new User();
        if (notification.getType() == Notification.NotificationType.LIKE) {
            sender = likeService.findById(id).getUser();
        }
        if (notification.getType() == Notification.NotificationType.COMMENT) {
            sender = commentService.findById(id).getUser();
        }
        if (notification.getType() == Notification.NotificationType.FOLLOW) {
            sender = followService.findById(id).getFollower();
        }
        return sender;
    }

    public String buildMessage(NotificationResponseDTO dto){
        String text = "";
        if(dto.getType().equals("FOLLOW")){
            text = " bạn.";
        }else{
            text = " bài viết của bạn.";
        }
        String message = dto.getTriggerUsername().toUpperCase() + " đã " + dto.getType().toLowerCase() + text;
        return message;
    }

}
