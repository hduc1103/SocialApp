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

    private String extractUsername(String token) {
        String jwtToken = token.substring(7);
        return jwtUtil.extractUsername(jwtToken);
    }

    @PostMapping("/addComment")
    public ResponseEntity<?> addComment(@RequestHeader("Authorization") String token, @RequestParam Long postId,
                                        @RequestBody Map<String, String> text) {
        String username = extractUsername(token);
        String content = text.get("text");
        String response = interactService.addComment(postId, username, content);
        if (response.startsWith(ERROR_MSG)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/deleteComment")
    public ResponseEntity<?> deleteComment(@RequestParam Long cmtId) {
        String response = interactService.deleteComment(cmtId);
        if (response.startsWith(ERROR_MSG)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/updateComment")
    public void updateComment(@RequestParam Long commentId, @RequestBody String new_comment) {
        interactService.updateComment(commentId, new_comment);
    }

    @PostMapping("/like")
    public ResponseEntity<?> likePost(@RequestHeader("Authorization") String token, @RequestParam Long postId) {
        String username = extractUsername(token);
        String result = interactService.likePost(username, postId);
        if (result.equals(Y_LIKE)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("/dislike")
    public ResponseEntity<?> dislikePost(@RequestHeader("Authorization") String token, @RequestParam Long postId) {
        String username = extractUsername(token);
        String result = interactService.dislikePost(username, postId);

        if (result.equals(N_LIKE)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/getCommentUser/{commentId}")
    public ResponseEntity<String> getCommentUser(@PathVariable long commentId) {
        return ResponseEntity.status(HttpStatus.OK).body(interactService.getCommentAuthor(commentId));
    }
}
