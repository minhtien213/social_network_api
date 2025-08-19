package com.example.social_network_api.service.impl;

import com.example.social_network_api.entity.Like;
import com.example.social_network_api.entity.Post;
import com.example.social_network_api.entity.User;
import com.example.social_network_api.exception.custom.BadRequestException;
import com.example.social_network_api.exception.custom.ResourceNotfoundException;
import com.example.social_network_api.repository.LikeRepository;
import com.example.social_network_api.service.LikeService;
import com.example.social_network_api.service.PostService;
import com.example.social_network_api.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final PostService postService;
    private final UserService userService;

    @Transactional
    public Like createLike(Long postId, String username) {
        Post post = postService.findById(postId);
        User user = userService.findByUsername(username);
        if(likeRepository.existsByPostIdAndUserId(postId, user.getId())){
            throw new BadRequestException("Like already exists");
        }
        Like like = Like.builder()
                .post(post)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();
        return likeRepository.save(like);
    }

    @Override
    @Transactional
    public void deleteByPostId(Long postId, String username) {
        Post post = postService.findById(postId);
        if (post == null) {
            throw new ResourceNotfoundException("Post not found");
        }

        User user = userService.findByUsername(username);
        if (user == null) {
            throw new ResourceNotfoundException("User not found");
        }

        if (!likeRepository.existsByPostIdAndUserId(postId, user.getId())) {
            throw new BadRequestException("Like not found or UnAuthorized");
        }

        likeRepository.deleteByPostIdAndUserId(postId, user.getId());
    }

    @Override
    public void deleteById(Long id) {
        if(!likeRepository.findById(id).isEmpty()) {
            throw new ResourceNotfoundException("Like not found");
        }
        likeRepository.deleteById(id);
    }

    @Override
    public List<Like> findAll() {
        return likeRepository.findAll();
    }

    @Override
    public Like findById(Long id) {
        return likeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotfoundException("Like not found")
        );
    }
}
