package com.example.social_network_api.service;

import com.example.social_network_api.entity.Like;

public interface LikeService extends IService<Like> {
    Like createLike(Long postId, String username);
    void deleteByPostId(Long postId, String username);
}
