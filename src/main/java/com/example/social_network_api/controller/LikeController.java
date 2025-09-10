package com.example.social_network_api.controller;

import com.example.social_network_api.entity.Like;
import com.example.social_network_api.mapper.LikeMapper;
import com.example.social_network_api.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/like")
public class LikeController {
    private  final LikeService likeService;
    private final LikeMapper likeMapper;

    @PostMapping("/postId/{postId}")
    public ResponseEntity<?> createLike(@PathVariable Long postId, Principal principal){
        Like savedLike = likeService.createLike(postId, principal.getName());
        return ResponseEntity.ok(likeMapper.toLikeResponseDTO(savedLike));
    }

    @DeleteMapping("/postId/{postId}")
    public ResponseEntity<?> unLikePostId(@PathVariable Long postId, Principal principal){
        likeService.unLikePostId(postId, principal.getName());
        return ResponseEntity.ok("Like delete successfully");
    }

    // lấy danh sách tên đã like bài post
    @GetMapping("/postId/{postId}")
    public ResponseEntity<?> getUsernameLikedPost(@PathVariable Long postId){
        return ResponseEntity.ok(likeService.getUsernameLikedPost(postId));
    }

    // đếm số like bài post
    @GetMapping("/postId/{postId}/count")
    public ResponseEntity<?> getLikedPostCount(@PathVariable Long postId){
        return ResponseEntity.ok(likeService.getLikedPostCount(postId)) ;
    }

    //kiểm tra user đã like bài post chưa
    @GetMapping("/postId/{postId}/me")
    public ResponseEntity<?> getLikedPostMe(@PathVariable Long postId, Principal principal){
        return ResponseEntity.ok(likeService.existsByPostIdAndUserId(postId, principal.getName()));
    }
}
