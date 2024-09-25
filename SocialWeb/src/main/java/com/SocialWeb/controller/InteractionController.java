package com.SocialWeb.controller;

import com.SocialWeb.security.JwtUtil;
import com.SocialWeb.service.InteractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

import static com.SocialWeb.Message.*;

@RestController
@RequestMapping("/interact")
public class InteractionController {
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    InteractService interactService;

    @PostMapping("/addComment")
    public ResponseEntity<?> addComment(@RequestHeader("Authorization") String token, @RequestParam Long postId,
            @RequestBody Map<String, String> text) {
        String jwtToken = token.substring(7);
        String username = jwtUtil.extractUsername(jwtToken);
        String content = text.get("text");
        String response = interactService.addComment(postId, username, content);
        if (response.startsWith(ERROR_MSG)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/deleteComment")
    public ResponseEntity<?> deleteComment(@RequestHeader("Authorization") String token, @RequestParam Long postId,
            @RequestParam Long cmtId) {
        String jwtToken = token.substring(7);
        String username = jwtUtil.extractUsername(jwtToken);
        String response = interactService.deleteComment(postId, username, cmtId);
        if (response.startsWith(ERROR_MSG)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/updateComment")
    public void updateComment(@RequestHeader("Authorization") String token, @RequestParam Long commentId, @RequestBody String new_comment){
        String jwtToken = token.substring(7);
        String username = jwtUtil.extractUsername(jwtToken);
        interactService.updateComment(commentId, new_comment);
    }
    @PostMapping("/like")
    public ResponseEntity<?> likePost(@RequestHeader("Authorization") String token, @RequestParam Long postId) {
        String jwtToken = token.substring(7);
        String username = jwtUtil.extractUsername(jwtToken);
        String result = interactService.likePost(username, postId);

        if (result.equals(Y_LIKE)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
    @PostMapping("/dislike")
    public ResponseEntity<?> dislikePost(@RequestHeader("Authorization") String token, @RequestParam Long postId) {
        String jwtToken = token.substring(7);
        String username = jwtUtil.extractUsername(jwtToken);
        String result = interactService.dislikePost(username, postId);

        if (result.equals(N_LIKE)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
