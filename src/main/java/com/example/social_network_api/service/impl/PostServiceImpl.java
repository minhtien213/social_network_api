package com.example.social_network_api.service.impl;

import com.example.social_network_api.config.UploadsUtils;
import com.example.social_network_api.dto.request.PostRequestDTO;
import com.example.social_network_api.entity.Post;
import com.example.social_network_api.entity.Role;
import com.example.social_network_api.entity.User;
import com.example.social_network_api.mapper.PostMapper;
import com.example.social_network_api.repository.PostRepository;
import com.example.social_network_api.repository.RoleRepository;
import com.example.social_network_api.service.PostService;
import com.example.social_network_api.service.RoleService;
import com.example.social_network_api.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserService userService;
    private final PostMapper postMapper;

    @Override
    @Transactional
    public Post save(Post post) {
        return postRepository.save(post);
    }

    @Override
    public Post update(Long id, Post post) {
        return null;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Post with id " + id + " not found")
        );
        postRepository.delete(post);
    }

    @Override
    public List<Post> findAll() {
        return postRepository.findAll();
    }

    @Override
    public Post findById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Post with id " + id + " not found")
        );
        return post;
    }


    @Override
    @Transactional
    public Post createPost(PostRequestDTO postRequestDTO, String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        //list link file ảnh (string)
        String mediaPathsString = UploadsUtils.uploadFiles(postRequestDTO.getMediaUrls());

        Post post = postMapper.toPost(postRequestDTO, user, mediaPathsString);
        return postRepository.save(post);
    }

    @Transactional
    public Post updatePost(Long id, PostRequestDTO postRequestDTO, String username) {
        Post existingPost = postRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Post with id " + id + " not found")
        );
        if (!existingPost.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized user");
        }

        String mediaPathsString = UploadsUtils.uploadFiles(postRequestDTO.getMediaUrls());

        if (existingPost.getMediaUrls() != null && !existingPost.getMediaUrls().isEmpty()) {
            String existingFile = existingPost.getMediaUrls();
            mediaPathsString.concat(existingFile);
        }
        existingPost.setContent(postRequestDTO.getContent());
        existingPost.setMediaUrls(mediaPathsString);
        existingPost.setUpdatedAt(LocalDateTime.now());
        return postRepository.save(existingPost);
    }
}
