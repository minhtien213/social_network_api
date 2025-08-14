package com.example.social_network_api.service;

import com.example.social_network_api.dto.request.PostRequestDTO;
import com.example.social_network_api.entity.Post;
import org.springframework.stereotype.Repository;

@Repository
public interface PostService extends IService<Post> {
    Post createPost(PostRequestDTO postRequestDTO, String username);
    Post updatePost(Long id, PostRequestDTO postRequestDTO, String username);
}

