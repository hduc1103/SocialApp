package com.SocialWeb.controller;

import com.SocialWeb.entity.Post;
import com.SocialWeb.security.JwtUtil;
import com.SocialWeb.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/posts")
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
}
