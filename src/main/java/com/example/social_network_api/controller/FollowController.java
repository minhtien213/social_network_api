package com.example.social_network_api.controller;

import com.example.social_network_api.dto.request.FollowRequestDTO;
import com.example.social_network_api.dto.respone.FollowResponseDTO;
import com.example.social_network_api.dto.respone.UserResponseDTO;
import com.example.social_network_api.entity.Follow;
import com.example.social_network_api.entity.User;
import com.example.social_network_api.mapper.FollowMapper;
import com.example.social_network_api.mapper.UserMapper;
import com.example.social_network_api.service.FollowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/follows")
public class FollowController {

    private final FollowService followService;
    private final FollowMapper followMapper;
    private final UserMapper userMapper;

    @PostMapping("/create")
    public ResponseEntity<?> createFollow(@Valid @RequestBody FollowRequestDTO followRequestDTO) {
        Follow savedFollow = followService.createFollow(
                followRequestDTO.getFollowerId(),
                followRequestDTO.getFollowingId());
        return ResponseEntity.ok().body(followMapper.toFollowResponseDTO(savedFollow));
    }

    @PostMapping("/{followId}/accept")
    public ResponseEntity<?> acceptFollow(@PathVariable Long followId) {
        Follow acceptedFollow = followService.acceptFollow(followId);
        return ResponseEntity.ok().body(followMapper.toFollowResponseDTO(acceptedFollow));
    }

    @PostMapping("/{followId}/reject")
    public ResponseEntity<?> rejectFollow(@PathVariable Long followId) {
        Follow rejectedFollow = followService.rejectFollow(followId);
        return ResponseEntity.ok().body(followMapper.toFollowResponseDTO(rejectedFollow));
    }

    @GetMapping("/check")
    public ResponseEntity<?> isFriends(@Valid @RequestBody FollowRequestDTO followRequestDTO) {
        Map<String, Boolean> isFriend = followService.isFriend(followRequestDTO.getFollowerId(), followRequestDTO.getFollowingId());
        return ResponseEntity.ok().body(isFriend);
    }

    @DeleteMapping("/{followId}/cancel")
    public ResponseEntity<?> cancelRequest(@PathVariable Long followId) {
        followService.cancelRequest(followId);
        return ResponseEntity.ok("Revok request successfully.");
    }

    @DeleteMapping("/{followId}/unfollow")
    public ResponseEntity<?> unFollow(@PathVariable Long followId) {
        followService.unFollow(followId);
        return ResponseEntity.ok("Unfollow successfully.");
    }

    @GetMapping("/{userId}/friend-counts")
    public ResponseEntity<?> getFriendCounts(@PathVariable Long userId) {
        Map<String, Long> count =  followService.getFriendsCount(userId);
        return ResponseEntity.ok().body(count);
    }

    @GetMapping("/{userId}/friends")
    public ResponseEntity<Page<?>> getAllFriends(@PathVariable Long userId,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "3") int size) {
        Page<User> friends = followService.getAllFriends(userId, page, size);
        Page<UserResponseDTO> friendsDTO = friends.map(userMapper::toUserResponseDTO);
        return ResponseEntity.ok().body(friendsDTO);
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<?> getFollowers(@PathVariable Long userId) {
        Set<User> followers = followService.getFollowers(userId);
        Set<UserResponseDTO> followersDTO = followers.stream()
                .map(follower -> userMapper.toUserResponseDTO(follower)).collect(Collectors.toSet());
        return ResponseEntity.ok().body(followersDTO);
    }

    @GetMapping("/{userId}/followings")
    public ResponseEntity<?> getFollowings(@PathVariable Long userId) {
        Set<User> followings = followService.getFollowing(userId);
        Set<UserResponseDTO> followingsDTO = followings.stream()
                .map(following -> userMapper.toUserResponseDTO(following)).collect(Collectors.toSet());
        return ResponseEntity.ok().body(followingsDTO);
    }

    @GetMapping("/{userId}/pending-requests-sent")
    public ResponseEntity<?> getPendingRequestsSent(@PathVariable Long userId) {
        Set<User> pendingRequestsSent = followService.getPendingRequestsSent(userId);
        Set<UserResponseDTO> pendingRequestsSentDTO = pendingRequestsSent.stream()
                .map(pendingRequestSent -> userMapper.toUserResponseDTO(pendingRequestSent)).collect(Collectors.toSet());
        return ResponseEntity.ok().body(pendingRequestsSentDTO);
    }

    @GetMapping("/{userId}/pending-requests-received")
    public ResponseEntity<?> getPendingRequestsReceived(@PathVariable Long userId) {
        Set<User> pendingRequestsReceived = followService.getPendingRequestsReceived(userId);
        Set<UserResponseDTO> pendingRequestsReceivedDTO = pendingRequestsReceived.stream()
                .map(pendingRequestReceived -> userMapper.toUserResponseDTO(pendingRequestReceived)).collect(Collectors.toSet());
        return ResponseEntity.ok().body(pendingRequestsReceivedDTO);
    }


}
