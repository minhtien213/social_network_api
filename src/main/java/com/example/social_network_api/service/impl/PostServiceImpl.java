package com.example.social_network_api.service.impl;

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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserService userService;
    private final PostMapper postMapper;

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

    @Override
    public Post createPost(PostRequestDTO postRequestDTO, String username) {

        // Lấy user tạo post
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        String mediaPath = null;

        // Xử lý file nếu có
        if (postRequestDTO.getMediaUrl() != null && !postRequestDTO.getMediaUrl().isEmpty()) {
            try {
                // Tạo folder uploads nếu chưa tồn tại
                Path uploadDir = Paths.get("uploads");
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }

                // Tạo tên file duy nhất
                String fileName = UUID.randomUUID() + "_" + postRequestDTO.getMediaUrl().getOriginalFilename();
                Path filePath = uploadDir.resolve(fileName);

                // Lưu file
                Files.copy(postRequestDTO.getMediaUrl().getInputStream(), filePath);

                // Lưu path để lưu vào DB
                mediaPath = "/uploads/" + fileName;
            } catch (IOException e) {
                throw new RuntimeException("Cannot save file: " + e.getMessage());
            }
        }

        // dùng mapper để map từ DTO -> entity
        Post post = postMapper.toPost(postRequestDTO, user, mediaPath);

        return postRepository.save(post);
    }
}
