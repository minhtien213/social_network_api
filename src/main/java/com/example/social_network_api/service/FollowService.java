package com.example.social_network_api.service;

import com.example.social_network_api.entity.Follow;
import com.example.social_network_api.entity.User;
import com.example.social_network_api.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FollowService {
    Follow createFollow(Long followerId, Long followingId);
    Follow acceptFollow(Long followId);
    Follow rejectFollow(Long followId);
    void cancelRequest(Long followId);
    void unFollow(Long followId);

    Map<String, Boolean> isFriend(Long followerId, Long followingId);
    Map<String, Long> getFriendsCount(Long userId);

    Set<User> getAllFriends(Long userId);

    Set<User> getFollowers(Long userId);
    Set<User> getFollowing(Long userId);

    Set<User> getPendingRequestsSent(Long userId);
    Set<User> getPendingRequestsReceived(Long userId);

}
