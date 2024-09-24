package com.SocialWeb.controller;

import com.SocialWeb.security.JwtUtil;
import com.SocialWeb.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import static com.SocialWeb.config.Message.ERROR_MSG;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    CommentService commentService;

    @PostMapping("/addComment")
    public ResponseEntity<?> addComment(@RequestHeader("Authorization") String token, @RequestParam Long postId, @RequestBody Map<String, String> text) {
        String jwtToken = token.substring(7);
        String username = jwtUtil.extractUsername(jwtToken);
        String content = text.get("text");
        String response = commentService.addComment(postId, username, content);
        if (response.startsWith(ERROR_MSG)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PostMapping("deleteComment")
    public ResponseEntity<?> deleteComment(@RequestHeader("Authorization") String token, @RequestParam Long postId, @RequestParam Long cmtId){
        String jwtToken = token.substring(7);
        String username = jwtUtil.extractUsername(jwtToken);
        String response = commentService.deleteComment(postId, username,  cmtId);
        if(response.startsWith(ERROR_MSG)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
