package com.SocialWeb.controller;

import com.SocialWeb.domain.response.CommentResponse;
import com.SocialWeb.domain.response.PostResponse;
import com.SocialWeb.entity.PostEntity;
import com.SocialWeb.security.JwtUtil;
import com.SocialWeb.service.interfaces.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/post")
public class PostController {

    private final PostService postService;
    private final JwtUtil jwtUtil;

    public PostController(PostService postService, JwtUtil jwtUtil){
        this.postService = postService;
        this.jwtUtil = jwtUtil;
    }

    private String extractUsername(String token) {
        String jwtToken = token.substring(7);
        return jwtUtil.extractUsername(jwtToken);
    }

    @GetMapping("/getPostById")
    public PostEntity getPostById(@RequestParam("postId") long postId) {
        return postService.getPostById(postId);
    }

    @GetMapping("/getUserPost")
    public List<PostResponse> getUserPosts(@RequestParam("userId") long userId) {
        List<PostEntity> postEntities = postService.getPostsByUser(userId);
        return postEntities.stream()
                .map(postEntity -> PostResponse.builder()
                        .id(postEntity.getId())
                        .content(postEntity.getContent())
                        .createdAt(postEntity.getCreatedAt())
                        .updatedAt(postEntity.getUpdatedAt())
                        .userId(postEntity.getUser().getId())
                        .comments(postEntity.getComments().stream()
                                .map(commentEntity -> CommentResponse.builder()
                                        .id(commentEntity.getId())
                                        .user_id(commentEntity.getUser().getId())
                                        .text(commentEntity.getText())
                                        .createdAt(commentEntity.getCreatedAt())
                                        .updatedAt(commentEntity.getUpdatedAt())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

    @GetMapping("/numberOfLikes")
    public long getLikeCount(@RequestParam("postId") long postId) {
        return postService.numberOfLikes(postId);
    }

    @PostMapping("/createPost")
    public ResponseEntity<?> createPost(@RequestHeader("Authorization") String token,
                                        @RequestBody Map<String, String> postData) {
        String username = extractUsername(token);
        String content = postData.get("content");
        return ResponseEntity.status(HttpStatus.OK).body(postService.createPost(username, content));
    }

    @DeleteMapping("/deletePost")
    public ResponseEntity<?> deletePost(@RequestParam("postId") long postId) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.deletePost(postId));
    }

    @PutMapping("/updatePost")
    public ResponseEntity<?> updatePost(@RequestParam("postId") long postId, @RequestBody String newContent) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.updatePost(postId, newContent));
    }

    @GetMapping("/retrieveFriendsPosts")
    public ResponseEntity<List<PostResponse>> retrieveFriendsPosts(@RequestParam("userId") long userId) {
        List<PostEntity> postEntities = postService.retrieveRecentFriendPosts(userId);
        List<PostResponse> postResponses = postEntities.stream()
                .map(postEntity -> PostResponse.builder()
                        .id(postEntity.getId())
                        .content(postEntity.getContent())
                        .createdAt(postEntity.getCreatedAt())
                        .updatedAt(postEntity.getUpdatedAt())
                        .userId(postEntity.getUser().getId())
                        .comments(postEntity.getComments().stream()
                                .map(commentEntity -> CommentResponse.builder()
                                        .id(commentEntity.getId())
                                        .user_id(commentEntity.getUser().getId())
                                        .text(commentEntity.getText())
                                        .createdAt(commentEntity.getCreatedAt())
                                        .updatedAt(commentEntity.getUpdatedAt())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(postResponses);
    }

}


