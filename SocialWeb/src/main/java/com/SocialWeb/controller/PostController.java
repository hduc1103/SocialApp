package com.SocialWeb.controller;

import com.SocialWeb.entity.PostEntity;
import com.SocialWeb.security.JwtUtil;
import com.SocialWeb.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/getUserPost")
    public List<PostEntity> getUserPosts(@RequestHeader("Authorization") String token) {
        String jwtToken = token.substring(7);
        String username = jwtUtil.extractUsername(jwtToken);
        return postService.getPostsByUser(username);
    }

    @GetMapping("/numberOfLikes")
    public long getLikeCount(@RequestParam("postId") long postId){
        return postService.numberOfLikes(postId);
    }

    @PostMapping("/createPost")
    public ResponseEntity<?> createPost(@RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> postData) {
        String jwtToken = token.substring(7);
        String username = jwtUtil.extractUsername(jwtToken);
        String content = postData.get("content");
        return ResponseEntity.status(HttpStatus.OK).body(postService.createPost(username, content));
    }

    @PostMapping("/deletePost")
    public ResponseEntity<?> deletePost(@RequestHeader("Authorization") String token, @RequestParam("postId") long postId){
        String jwtToken = token.substring(7);
        String username = jwtUtil.extractUsername(jwtToken);
        return ResponseEntity.status(HttpStatus.OK).body(postService.deletePost(postId));
    }

    @PostMapping("/updatePost")
    public ResponseEntity<?> updatePost(@RequestHeader("Authorization") String token, @RequestParam("postId") long postId, @RequestBody String newContent) {
        String jwtToken = token.substring(7);
        String username = jwtUtil.extractUsername(jwtToken);
        return ResponseEntity.status(HttpStatus.OK).body(postService.updatePost(postId, newContent));
    }
}


