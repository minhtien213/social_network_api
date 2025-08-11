package com.example.social_network_api.service.impl;

import com.example.social_network_api.entity.Post;
import com.example.social_network_api.entity.Role;
import com.example.social_network_api.repository.PostRepository;
import com.example.social_network_api.repository.RoleRepository;
import com.example.social_network_api.service.PostService;
import com.example.social_network_api.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {
    @Autowired
    private PostRepository postRepository;

    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public Post save(Post post) {
        return null;
    }

    @Override
    public Post update(Long id, Post post) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public List<Post> findAll() {
        return List.of();
    }

    @Override
    public Post findById(Long id) {
        return null;
    }
}
