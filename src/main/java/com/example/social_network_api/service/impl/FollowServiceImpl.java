package com.example.social_network_api.service.impl;

import com.example.social_network_api.entity.Follow;
import com.example.social_network_api.entity.User;
import com.example.social_network_api.exception.custom.BadRequestException;
import com.example.social_network_api.exception.custom.ForbiddenException;
import com.example.social_network_api.exception.custom.ResourceNotFoundException;
import com.example.social_network_api.repository.FollowRepository;
import com.example.social_network_api.service.FollowService;
import com.example.social_network_api.service.UserService;
import com.example.social_network_api.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final UserService userService;

    //gởi lời mời
    @Override
    public Follow createFollow(Long followerId, Long followingId) {
        if(followerId == null || followingId == null){
            throw  new BadRequestException("followerId and followingId can't be null");
        }

        if (followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw new BadRequestException("Follow request already exists.");
        }

        if(followerId.equals(followingId)){
            throw new BadRequestException("You cannot follow yourself.");
        }

        User follower = userService.findById(followerId);
        User following = userService.findById(followingId);
        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .followStatus(Follow.FollowStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        return followRepository.save(follow);
    }

    public Follow updateFollowStatus(Long followId, Follow.FollowStatus newStatus) {
        Follow existingFollow = followRepository.findById(followId)
                .orElseThrow(()-> new ResourceNotFoundException("Follow not found."));
        if (existingFollow.getFollowStatus() != Follow.FollowStatus.PENDING) {
            throw new BadRequestException("Request already processed");
        }
        existingFollow.setFollowStatus(newStatus);
        return followRepository.save(existingFollow);
    }

    //chấp nhận bạn bè
    @Override
    public Follow acceptFollow(Long followId) {
       return updateFollowStatus(followId, Follow.FollowStatus.ACCEPTED);
    }

    //từ chối lời mời
    @Override
    public Follow rejectFollow(Long followId) {
       return updateFollowStatus(followId, Follow.FollowStatus.REJECTED);
    }

    public void revokeFollow(Long followId, Follow.FollowStatus expectedStatus) {
        Follow existingFollow = followRepository.findById(followId)
                .orElseThrow(()-> new ResourceNotFoundException("Follow not found."));
        if(!existingFollow.getFollower().getUsername().equals(AuthUtils.getCurrentUsername())){
            throw new ForbiddenException("You are not allowed to revoke this follow request.");
        }
        if(existingFollow.getFollowStatus() != expectedStatus){
            throw new BadRequestException("You cannot revoke this follow because its status is " +
                    existingFollow.getFollowStatus());
        }
        followRepository.delete(existingFollow);
    }

    //hủy bỏ gởi lời mời
    @Override
    public void cancelRequest(Long followId) {
        revokeFollow(followId, Follow.FollowStatus.PENDING);
    }

    //hủy kết bạn
    @Override
    public void unFollow(Long followId) {
        revokeFollow(followId, Follow.FollowStatus.ACCEPTED);
    }

    //tổng số bạn bè
    @Override
    public Map<String, Long> getFriendsCount(Long userId) {
        User user = userService.findById(userId);
        return Map.of("friend_count", followRepository.countFriends(user.getId(), Follow.FollowStatus.ACCEPTED));
    }

    //kiểm tra bạn bè
    @Override
    public Map<String, Boolean> isFriend(Long followerId, Long followingId) {
        User currentUser = userService.findById(followerId);
        return Map.of("isFriend", followRepository.existsByFollowerIdAndFollowingIdAndFollowStatus(
                currentUser.getId(), followingId, Follow.FollowStatus.ACCEPTED
        ));
    }

    @Override
    public Set<User> getAllFriends(Long userId) {
        Set<User> friends = followRepository.findAllFriends(userId, Follow.FollowStatus.ACCEPTED);
//        Set<String> allFriends = friends.stream().map(User::getUsername).collect(Collectors.toSet());
        return friends;
    }

    @Override
    public Set<User> getFollowers(Long userId) {
        Set<User> followers = followRepository.findFollowers(
                userId,
                Set.of(Follow.FollowStatus.PENDING, Follow.FollowStatus.ACCEPTED)
        );
//        Set<String> usernames = followers.stream().map(User::getUsername).collect(Collectors.toSet());
        return followers;
    }

    @Override
    public Set<User> getFollowing(Long userId) {
        Set<User> followings = followRepository.getFollowings(
                userId,
                Set.of(Follow.FollowStatus.PENDING, Follow.FollowStatus.ACCEPTED)
        );
//        Set<String> usernames = followings.stream().map(User::getUsername).collect(Collectors.toSet());
        return followings;
    }

    @Override
    public Set<User> getPendingRequestsSent(Long userId) {
        Set<User> pendingRequestsSent = followRepository.findPendingRequestsSent(userId, Follow.FollowStatus.PENDING);
//        Set<String> usernames = pendingRequestsSent.stream().map(User::getUsername).collect(Collectors.toSet());
        return pendingRequestsSent;
    }

    @Override
    public Set<User> getPendingRequestsReceived(Long userId) {
        Set<User> pendingRequestsReceived = followRepository.findPendingRequestsReceived(userId, Follow.FollowStatus.PENDING);
//        Set<String> usernames = pendingRequestsReceived.stream().map(User::getUsername).collect(Collectors.toSet());
        return pendingRequestsReceived;
    }

}
