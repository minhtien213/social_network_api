package com.example.social_network_api.controller;

import com.example.social_network_api.entity.Like;
import com.example.social_network_api.mapper.LikeMapper;
import com.example.social_network_api.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

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
    public ResponseEntity<?> deleteLikeByPostId(@PathVariable Long postId, Principal principal){
        likeService.deleteByPostId(postId, principal.getName());
        return ResponseEntity.ok("Like deleted");
    }


}
