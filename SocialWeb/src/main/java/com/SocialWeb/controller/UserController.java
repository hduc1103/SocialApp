package com.SocialWeb.controller;

import com.SocialWeb.domain.response.AuthResponse;
import com.SocialWeb.entity.User;
import com.SocialWeb.service.CustomUserDetailService;
import com.SocialWeb.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.SocialWeb.security.JwtUtil;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private CustomUserDetailService customUserDetailService;

    @PostMapping("/addFriend")
    public String addFriend(@RequestParam Long userId, @RequestParam Long friendId) {
        return userService.addFriend(userId, friendId);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody User user) {

        logger.info("-----------Received request to create user: {}", user.getUsername());

        userService.createUser(user);
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
        final UserDetails userDetails = customUserDetailService.loadUserByUsername(user.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthResponse(jwt));
    }


    @GetMapping("/info")
    public ResponseEntity<User> getUserInfo(@RequestHeader("Authorization") String token) {
        String jwtToken = token.substring(7);
        String username = jwtUtil.extractUsername(jwtToken);

        Optional<User> user = userService.getUserByUsername(username);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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
