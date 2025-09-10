package com.example.social_network_api.service.impl;

import com.example.social_network_api.entity.Follow;
import com.example.social_network_api.entity.Notification;
import com.example.social_network_api.entity.User;
import com.example.social_network_api.exception.custom.BadRequestException;
import com.example.social_network_api.exception.custom.ForbiddenException;
import com.example.social_network_api.exception.custom.ResourceNotFoundException;
import com.example.social_network_api.repository.FollowRepository;
import com.example.social_network_api.service.FollowService;
import com.example.social_network_api.service.NotificationService;
import com.example.social_network_api.service.UserService;
import com.example.social_network_api.utils.AuthUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    //gởi lời mời
    @Override
    @Transactional
    public Follow createFollow(Long followerId, Long followingId) {
        if (followerId == null || followingId == null) {
            throw new BadRequestException("FollowerId and followingId can't be null");
        }

        Follow existingFollow = followRepository.existsFollow(followerId, followingId);

        if(existingFollow != null && (existingFollow.getFollowStatus() == Follow.FollowStatus.ACCEPTED ||
                        existingFollow.getFollowStatus() == Follow.FollowStatus.PENDING)) {
            throw new BadRequestException("Follow already exists");
        }

        if (existingFollow != null && existingFollow.getFollowStatus() == Follow.FollowStatus.REJECTED) {
            followRepository.delete(existingFollow);
        }

        if (followerId.equals(followingId)) {
            throw new BadRequestException("You cannot follow yourself.");
        }

        User follower = userService.findById(followerId);
        User following = userService.findById(followingId);

        if(!AuthUtils.getCurrentUsername().equals(follower.getUsername())) {
            throw new ForbiddenException("Unauthorized");
        }

        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .followStatus(Follow.FollowStatus.PENDING)
                .build();
        Follow savedFollow = followRepository.save(follow);

        User receiver = userService.findById(savedFollow.getFollowing().getId());

        notificationService.createAndSentNotification(savedFollow.getId(), receiver, Notification.NotificationType.FOLLOW);
        return savedFollow;
    }

    public Follow updateFollowStatus(Long followId, Follow.FollowStatus newStatus) {
        Follow existingFollow = followRepository.findById(followId)
                .orElseThrow(() -> new ResourceNotFoundException("Follow not found."));
        if(existingFollow.getFollower().getUsername().equals(AuthUtils.getCurrentUsername())){
            throw  new ForbiddenException("You cannot handle your own request.");
        }
        if (existingFollow.getFollowStatus() != Follow.FollowStatus.PENDING) {
            throw new BadRequestException("Request already processed");
        }
        existingFollow.setFollowStatus(newStatus);
        return followRepository.save(existingFollow);
    }

    //chấp nhận bạn bè
    @Override
    @Transactional
    public Follow acceptFollow(Long followId) {
        return updateFollowStatus(followId, Follow.FollowStatus.ACCEPTED);
    }

    //từ chối lời mời
    @Override
    @Transactional
    public Follow rejectFollow(Long followId) {
        return updateFollowStatus(followId, Follow.FollowStatus.REJECTED);
    }

    public void revokeFollow(Long followId, Follow.FollowStatus expectedStatus) {
        Follow existingFollow = followRepository.findById(followId)
                .orElseThrow(() -> new ResourceNotFoundException("Follow not found."));
        if (!existingFollow.getFollower().getUsername().equals(AuthUtils.getCurrentUsername())) {
            throw new ForbiddenException("You are not allowed to revoke this follow request.");
        }
        if (existingFollow.getFollowStatus() != expectedStatus) {
            throw new BadRequestException("You cannot revoke this follow because its status is " +
                    existingFollow.getFollowStatus());
        }
        followRepository.delete(existingFollow);
    }

    //hủy bỏ gởi lời mời
    @Override
    @Transactional
    public void cancelRequest(Long followId) {
        revokeFollow(followId, Follow.FollowStatus.PENDING);
    }

    //hủy kết bạn
    @Override
    @Transactional
    public void unFollow(Long followId) {
        revokeFollow(followId, Follow.FollowStatus.ACCEPTED);
    }

    //tổng số bạn bè
    @Override
    public Map<String, Long> getFriendsCount(Long userId) {
        User user = userService.findById(userId);
        if(!user.getUsername().equals(AuthUtils.getCurrentUsername()) && !AuthUtils.isAdmin()) {
            throw new ForbiddenException("Unauthorized request");
        }
        return Map.of("friend_count", followRepository.countFriends(user.getId(), Follow.FollowStatus.ACCEPTED));
    }

    //kiểm tra bạn bè
    @Override
    public Map<String, Boolean> isFriend(Long followerId, Long followingId) {
        User currentUser = userService.findById(followerId);
        return Map.of("isFriend", followRepository.isFriend(
                currentUser.getId(), followingId, Follow.FollowStatus.ACCEPTED
        ));
    }

    @Override
    public Page<User> getAllFriends(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<User> friends = followRepository.findAllFriends(userId, Follow.FollowStatus.ACCEPTED, pageable);
        return friends;
    }

    @Override
    public Set<User> getFollowers(Long userId) {
        User currentUser = userService.findById(userId);
        Set<User> followers = followRepository.findFollowers(
                currentUser.getId(), Follow.FollowStatus.ACCEPTED, Follow.FollowStatus.PENDING);
        return followers;
    }

    @Override
    public Set<User> getFollowing(Long userId) {
        User currentUser = userService.findById(userId);
        Set<User> followings = followRepository.getFollowings(
                currentUser.getId(), Follow.FollowStatus.ACCEPTED, Follow.FollowStatus.PENDING);
        return followings;
    }

    @Override
    public Set<User> getPendingRequestsSent(Long userId) {
        Set<User> pendingRequestsSent = followRepository.findPendingRequestsSent(userId, Follow.FollowStatus.PENDING);
        return pendingRequestsSent;
    }

    @Override
    public Set<User> getPendingRequestsReceived(Long userId) {
        Set<User> pendingRequestsReceived = followRepository.findPendingRequestsReceived(userId, Follow.FollowStatus.PENDING);
        return pendingRequestsReceived;
    }

    @Override
    public Follow findById(Long id) {
        return followRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Follow not found."));
    }
}
