package com.example.social_network_api.service.impl;

import com.example.social_network_api.config.UploadsUtils;
import com.example.social_network_api.dto.request.PostRequestDTO;
import com.example.social_network_api.entity.Post;
import com.example.social_network_api.entity.PostMedia;
import com.example.social_network_api.entity.Role;
import com.example.social_network_api.entity.User;
import com.example.social_network_api.exception.custom.ResourceNotfoundException;
import com.example.social_network_api.exception.custom.UnauthorizedException;
import com.example.social_network_api.mapper.PostMapper;
import com.example.social_network_api.repository.PostRepository;
import com.example.social_network_api.repository.RoleRepository;
import com.example.social_network_api.repository.UserRepository;
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
    private final UserRepository userRepository;

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
                () -> new ResourceNotfoundException("Post with id " + id + " not found")
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
                () -> new ResourceNotfoundException("Post with id " + id + " not found")
        );
        return post;
    }


    @Override
    @Transactional
    public Post createPost(PostRequestDTO postRequestDTO, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotfoundException("User not found"));

        Post post = new Post();
        post.setContent(postRequestDTO.getContent());
        post.setUser(user);

        List<PostMedia> postMediaList = new ArrayList<>();

        List<String> fileNames = UploadsUtils.uploadFiles(postRequestDTO.getFiles());
        fileNames.forEach(fileName -> {
            PostMedia postMedia = new PostMedia();
            postMedia.setMediaUrl(fileName);
            postMedia.setPost(post);
            postMediaList.add(postMedia);
        });

        post.setPostMediaList(postMediaList);

        return postRepository.save(post);
    }

    @Transactional
    public Post updatePost(Long id, PostRequestDTO postRequestDTO, String username) {
        Post existingPost = postRepository.findById(id).orElseThrow(
                () -> new ResourceNotfoundException("Post with id " + id + " not found")
        );

        if (!existingPost.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("Unauthorized user");
        }

        existingPost.setContent(postRequestDTO.getContent());
        existingPost.setUpdatedAt(LocalDateTime.now());

        // nếu client gửi files mới
        if (postRequestDTO.getFiles() != null) {
            //clear hết media cũ rồi add lại
            existingPost.getPostMediaList().clear();

            List<String> fileNames = UploadsUtils.uploadFiles(postRequestDTO.getFiles());
            fileNames.forEach(fileName -> {
                PostMedia postMedia = new PostMedia();
                postMedia.setMediaUrl(fileName);
                postMedia.setPost(existingPost);
                existingPost.getPostMediaList().add(postMedia);
            });
        }
        return postRepository.save(existingPost);
    }
}
