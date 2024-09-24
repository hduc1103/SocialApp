package com.SocialWeb.controller;

import com.SocialWeb.security.JwtUtil;
import com.SocialWeb.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static com.SocialWeb.config.Message.*;

@RestController
@RequestMapping("/api/like")
public class LikeController {

    @Autowired
    LikeService likeService;

    @Autowired
    JwtUtil jwtUtil;

    @PostMapping("/addLike")
    public ResponseEntity<?> likePost(@RequestHeader("Authorization") String token, @RequestParam Long postId) {
        String jwtToken = token.substring(7);
        String username = jwtUtil.extractUsername(jwtToken);
        String result = likeService.likePost(username, postId);

        if (result.equals(Y_LIKE)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("/removeLike")
    public ResponseEntity<?> dislikePost(@RequestHeader("Authorization") String token, @RequestParam Long postId) {
        String jwtToken = token.substring(7);
        String username = jwtUtil.extractUsername(jwtToken);
        String result = likeService.dislikePost(username, postId);

        if (result.equals(N_LIKE)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
