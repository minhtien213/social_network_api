package com.example.social_network_api.controller;

import com.example.social_network_api.dto.request.PostRequestDTO;
import com.example.social_network_api.dto.respone.PostResponseDTO;
import com.example.social_network_api.entity.Post;
import com.example.social_network_api.mapper.PostMapper;
import com.example.social_network_api.service.PostService;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    public ResponseEntity<Page<?>> getAllPosts(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "3") int size) {
        Page<Post> posts = postService.findAll(page, size);
        Page<PostResponseDTO> postResponseDTO = posts.map(postMapper::toPostResponseDTO);
        return ResponseEntity.ok(postResponseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        postService.deleteById(id);
        return ResponseEntity.ok("Post has been deleted");
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<?>> findAllByUserId(@PathVariable Long userId,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "3") int size){
        Page<Post> posts = postService.findAllByUserId(userId, page, size);
        Page<PostResponseDTO> dtos = posts.map(postMapper::toPostResponseDTO);
        return ResponseEntity.ok(dtos);
    }

}
