package com.SocialWeb.controller;

import com.SocialWeb.dto.PostDTO;
import com.SocialWeb.entity.Post;
import com.SocialWeb.security.JwtUtil;
import com.SocialWeb.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/post")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/user")
    public List<Post> getUserPosts(@RequestHeader("Authorization") String token) {
        String jwtToken = token.substring(7);
        String username = jwtUtil.extractUsername(jwtToken);
        return postService.getPostsByUser(username);
    }

    @GetMapping("/likeCount")
    public long getLikeCount(@RequestParam("postId") long postId){
        return postService.numberOfLikes(postId);
    }
    @PostMapping("/create")
    public ResponseEntity<?> createPost(@RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> postData) {
        String jwtToken = token.substring(7);
        String username = jwtUtil.extractUsername(jwtToken);
        String content = postData.get("content");
        return ResponseEntity.status(HttpStatus.OK).body(postService.createPost(username, content));
    }
}


