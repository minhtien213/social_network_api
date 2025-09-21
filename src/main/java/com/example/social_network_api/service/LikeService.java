package com.example.social_network_api.service;

import com.example.social_network_api.entity.Like;

import java.util.List;
import java.util.Map;

public interface LikeService extends IService<Like> {
    Like createLike(Long postId, String username);
    void unLikePostId(Long postId, String username);
    List<String> getUsernameLikedPost(Long postId);
    Map<String, Long> countLikedByPostId(Long postId);
    Map<String, Boolean> existsByPostIdAndUserId(Long postId, String username);
}
