package com.SocialWeb.controller;

import com.SocialWeb.entity.User;
import com.SocialWeb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/addFriend")
    public String addFriend(@RequestParam Long userId, @RequestParam Long friendId) {
        return userService.addFriend(userId, friendId);
    }

    @PostMapping("/post")
    public String createPost(@RequestParam Long userId, @RequestBody String content) {
        return userService.createPost(userId, content);
    }

    @PostMapping("/comment")
    public String addComment(@RequestParam Long postId, @RequestParam Long userId, @RequestBody String text) {
        return userService.addComment(postId, userId, text);
    }

    @PostMapping("/like")
    public String likePost(@RequestParam Long postId, @RequestParam Long userId) {
        return userService.likePost(postId, userId);
    }
}
