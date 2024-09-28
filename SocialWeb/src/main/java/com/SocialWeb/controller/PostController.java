package com.SocialWeb.controller;

import com.SocialWeb.domain.response.CommentResponse;
import com.SocialWeb.domain.response.PostResponse;
import com.SocialWeb.entity.PostEntity;
import com.SocialWeb.security.JwtUtil;
import com.SocialWeb.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private JwtUtil jwtUtil;

    private String extractUsername(String token){
        String jwtToken = token.substring(7);
        return jwtUtil.extractUsername(jwtToken);
    }

    @GetMapping("/getUserPost")
    public List<PostResponse> getUserPosts(@RequestHeader("Authorization") String token) {
        String username = extractUsername(token);
        List<PostEntity> postEntities = postService.getPostsByUser(username);
        return postEntities.stream()
                .map(postEntity -> PostResponse.builder()
                        .id(postEntity.getId())
                        .content(postEntity.getContent())
                        .createdAt(postEntity.getCreatedAt())
                        .updatedAt(postEntity.getUpdatedAt())
                        .user_id(postEntity.getUser().getId())
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
    public long getLikeCount(@RequestParam("postId") long postId){
        return postService.numberOfLikes(postId);
    }

    @PostMapping("/createPost")
    public ResponseEntity<?> createPost(@RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> postData) {
        String username = extractUsername(token);
        String content = postData.get("content");
        return ResponseEntity.status(HttpStatus.OK).body(postService.createPost(username, content));
    }

    @DeleteMapping ("/deletePost")
    public ResponseEntity<?> deletePost(@RequestHeader("Authorization") String token, @RequestParam("postId") long postId){
        return ResponseEntity.status(HttpStatus.OK).body(postService.deletePost(postId));
    }

    @PutMapping("/updatePost")
    public ResponseEntity<?> updatePost(@RequestParam("postId") long postId, @RequestBody String newContent) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.updatePost(postId, newContent));
    }
}


