package com.example.social_network_api.service.impl;

import com.example.social_network_api.exception.custom.ForbiddenException;
import com.example.social_network_api.repository.CommentRepository;
import com.example.social_network_api.repository.LikeRepository;
import com.example.social_network_api.repository.PostMediaRepository;
import com.example.social_network_api.service.UserService;
import com.example.social_network_api.utils.AuthUtils;
import com.example.social_network_api.utils.UploadsUtils;
import com.example.social_network_api.dto.request.PostRequestDTO;
import com.example.social_network_api.entity.Post;
import com.example.social_network_api.entity.PostMedia;
import com.example.social_network_api.entity.User;
import com.example.social_network_api.exception.custom.ResourceNotFoundException;
import com.example.social_network_api.exception.custom.UnauthorizedException;
import com.example.social_network_api.repository.PostRepository;
import com.example.social_network_api.service.PostService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final PostMediaRepository postMediaRepository;

    @Override
    @Transactional
    public void deleteById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Post with id " + id + " not found")
        );
        deleteMediaFromDir(id);
        postRepository.delete(post);
    }

    @Override
    public Page<Post> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return postRepository.findAll(pageable);
    }

    @Override
    public Post findById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Post with id " + id + " not found")
        );
        return post;
    }


    @Override
    @Transactional
    public Post createPost(PostRequestDTO postRequestDTO, String username) {
        User user = userService.findByUsername(username);
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
                () -> new ResourceNotFoundException("Post with id " + id + " not found")
        );

        if (!existingPost.getUser().getUsername().equals(username) && !AuthUtils.isAdmin()) {
            throw new UnauthorizedException("Unauthorized user");
        }

        existingPost.setContent(postRequestDTO.getContent());

        // nếu client gửi files mới
        if (postRequestDTO.getFiles() != null) {
            // xóa file trong /upload
            deleteMediaFromDir(id);

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

    @Override
    public Page<Post> findAllByUserId(Long userId, int page, int size) {
        User existingUser = userService.findById(userId);
        if (!AuthUtils.getCurrentUsername().equals(existingUser.getUsername())) {
            throw new ForbiddenException("Unauthorized");
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return postRepository.findAllByUserId(userId, pageable);
    }

    @Override
    public Map<String, Long> getCountLikeAndComment(Long postId) {
        Post existingPost = this.findById(postId);
        Long likeCount = likeRepository.countLikedByPostId(existingPost.getId());
        Long commentCount = commentRepository.countCommentByPostId(existingPost.getId());

        return Map.of("likeCount", likeCount,
                "commentCount", commentCount);
    }

    void deleteMediaFromDir(Long id){
        List<PostMedia> postMedias = postMediaRepository.findAllByPostId(id);
        if(postMedias != null && !postMedias.isEmpty()){
            postMedias.stream().forEach(postMedia -> {
                String mediaUrl = postMedia.getMediaUrl();
                UploadsUtils.deleteFile(mediaUrl);
            });
        }
    }
}
