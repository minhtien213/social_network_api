package com.example.social_network_api.controller;

import com.example.social_network_api.dto.request.CommentRequestDTO;
import com.example.social_network_api.dto.respone.CommentResponseDTO;
import com.example.social_network_api.entity.Comment;
import com.example.social_network_api.entity.User;
import com.example.social_network_api.mapper.CommentMapper;
import com.example.social_network_api.service.CommentService;
import jakarta.validation.Valid;
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
@RequestMapping("/api/comment")
public class CommentController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;

    @PostMapping("/create/{postId}")
    public ResponseEntity<?> createComment(@PathVariable("postId") Long postId,
                                 @Valid @RequestPart(value = "content", required = false) String content,
                                 @RequestPart(value = "mediaUrl", required = false) MultipartFile mediaUrl,
                                 Principal principal) {

        CommentRequestDTO commentRequestDTO = CommentRequestDTO.builder()
                .content(content)
                .mediaUrl(mediaUrl)
                .build();
        Comment savedComment = commentService.createComment(postId, commentRequestDTO, principal.getName());
        return ResponseEntity.ok(commentMapper.toCommentResponseDTO(savedComment));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateComment(@PathVariable Long id,
                                           @Valid @RequestPart(value = "content", required = false) String content,
                                           @RequestPart(value = "mediaUrl", required = false) MultipartFile mediaUrl,
                                           Principal principal){
        CommentRequestDTO commentRequestDTO = CommentRequestDTO.builder()
                .content(content)
                .mediaUrl(mediaUrl)
                .build();
        Comment updatedComment = commentService.updateComment(id, commentRequestDTO, principal.getName());
        return ResponseEntity.ok(commentMapper.toCommentResponseDTO(updatedComment));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getComment(@PathVariable Long id, Principal principal) {
        Comment comment = commentService.findById(id);
        return ResponseEntity.ok(commentMapper.toCommentResponseDTO(comment));
    }

    @GetMapping("/postId/{postId}")
    public ResponseEntity<List<?>> getCommentsByPostId(@PathVariable Long postId) {
        List<Comment> comments = commentService.getCommentsByPostId(postId);
        List<CommentResponseDTO> commentResponseDTOS = comments.stream()
                .map(commentMapper::toCommentResponseDTO).collect(Collectors.toList());
        return ResponseEntity.ok(commentResponseDTOS);
    }



    @GetMapping("/list-comments")
    public ResponseEntity<Page<?>> getAllComments(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "3") int size) {
        Page<Comment> comments = commentService.findAll(page, size);
        Page<CommentResponseDTO> commentResponseDTOS = comments.map(commentMapper::toCommentResponseDTO);
        return ResponseEntity.ok(commentResponseDTOS);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        commentService.deleteById(id);
        return ResponseEntity.ok("Comment has been deleted");
    }

}
