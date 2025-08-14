package com.example.social_network_api.controller;

import com.example.social_network_api.dto.request.PostRequestDTO;
import com.example.social_network_api.entity.Post;
import com.example.social_network_api.entity.User;
import com.example.social_network_api.mapper.PostMapper;
import com.example.social_network_api.repository.PostRepository;
import com.example.social_network_api.service.PostService;
import com.example.social_network_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {
    private final PostRepository postRepository;
    private final UserService userService;
    private final PostService postService;
    private final PostMapper postMapper;

    @PostMapping("/create")
    public ResponseEntity<?> createPost(
            @RequestPart("content") String content,
            @RequestPart(value = "mediaUrl", required = false) MultipartFile media,
            Principal principal
    ) {
        PostRequestDTO postRequestDTO = new PostRequestDTO();
        postRequestDTO.setContent(content);
        postRequestDTO.setMediaUrl(media);

        Post savedPost = postService.createPost(postRequestDTO, principal.getName());
        return ResponseEntity.ok(postMapper.toPostResponseDTO(savedPost));

    }
}
