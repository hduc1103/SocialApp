package com.SocialWeb.controller;

import com.SocialWeb.domain.response.CommentResponse;
import com.SocialWeb.service.interfaces.InteractService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.SocialWeb.Message.*;

@RestController
@RequestMapping("/interact")
public class InteractionController {

    private final InteractService interactService;
    public InteractionController(InteractService interactService){
        this.interactService = interactService;
    }

    @PostMapping("/add-comment")
    public ResponseEntity<?> addComment(@RequestHeader("Authorization") String token,
                                        @RequestParam Long postId,
                                        @RequestBody Map<String, String> text) {
        CommentResponse commentResponse = interactService.addComment(postId, token, text);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentResponse);
    }


    @DeleteMapping("/delete-comment")
    public ResponseEntity<?> deleteComment(@RequestHeader("Authorization") String token, @RequestParam Long cmtId) {
        String response = interactService.deleteComment(token, cmtId);
        if (response.startsWith(ERROR_MSG)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/update-comment")
    public void updateComment(@RequestHeader("Authorization") String token, @RequestParam Long commentId, @RequestBody Map<String, String> new_comment) {
        interactService.updateComment(token, commentId, new_comment);
    }

    @PostMapping("/like")
    public ResponseEntity<?> likePost(@RequestHeader("Authorization") String token, @RequestParam Long postId) {
        String result = interactService.likePost(token, postId);
        if (result.equals(Y_LIKE)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("/dislike")
    public ResponseEntity<?> dislikePost(@RequestHeader("Authorization") String token, @RequestParam Long postId) {
        String result = interactService.dislikePost(token, postId);
        if (result.equals(N_LIKE)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/get-comment-user/{commentId}")
    public ResponseEntity<String> getCommentUser(@PathVariable long commentId) {
        return ResponseEntity.status(HttpStatus.OK).body(interactService.getCommentAuthor(commentId));
    }
}
