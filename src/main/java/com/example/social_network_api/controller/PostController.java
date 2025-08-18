package com.example.social_network_api.controller;

import com.example.social_network_api.dto.request.PostRequestDTO;
import com.example.social_network_api.dto.respone.PostResponseDTO;
import com.example.social_network_api.entity.Post;
import com.example.social_network_api.entity.User;
import com.example.social_network_api.mapper.PostMapper;
import com.example.social_network_api.repository.PostRepository;
import com.example.social_network_api.service.PostService;
import com.example.social_network_api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

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
            //client gởi lên 2 field: content - json / files - MultipartFile
            @Valid @RequestPart("content") String content,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            Principal principal
    ) {
        PostRequestDTO postRequestDTO = new PostRequestDTO();
        postRequestDTO.setContent(content);
        postRequestDTO.setFiles(files);

        Post savedPost = postService.createPost(postRequestDTO, principal.getName());
        return ResponseEntity.ok(postMapper.toPostResponseDTO(savedPost));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(@PathVariable Long id,
                                        @RequestPart("content") String content,
                                        @RequestPart(value = "files", required = false) List<MultipartFile> files,
                                        Principal principal
    ) {
        PostRequestDTO postRequestDTO = PostRequestDTO.builder()
                .content(content)
                .files(files)
                .build();
        Post updatedPost = postService.updatePost(id, postRequestDTO, principal.getName());
        return ResponseEntity.ok(postMapper.toPostResponseDTO(updatedPost));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPost(@PathVariable Long id) {
        Post post = postService.findById(id);
        return ResponseEntity.ok(postMapper.toPostResponseDTO(post));
    }

    @GetMapping("/list-posts")
    public ResponseEntity<?> getAllPosts() {
        List<Post> posts = postService.findAll();
        List<PostResponseDTO> postResponseDTO = posts.stream()
                .map(post -> postMapper.toPostResponseDTO(post))
                .collect(Collectors.toList());
        return ResponseEntity.ok(postResponseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        postService.deleteById(id);
        return ResponseEntity.ok("Post has been deleted");
    }
}
