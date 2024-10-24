package com.socialweb.controller;

import com.socialweb.domain.response.PostResponse;
import com.socialweb.service.interfaces.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/post")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService){
        this.postService = postService;
    }
    
    @GetMapping("/get-user-post")
    public ResponseEntity<List<PostResponse>> getUserPosts(@RequestParam("userId") long userId) {
        try {
            List<PostResponse> postResponses = postService.getUserPosts(userId);
            return ResponseEntity.ok(postResponses);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/number-of-likes")
    public long getLikeCount(@RequestParam("postId") long postId) {
        return postService.numberOfLikes(postId);
    }

    @PostMapping("/create-post")
    public ResponseEntity<?> createPost(@RequestHeader("Authorization") String token,
                                        @RequestBody Map<String, String> postData) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.createPost(token, postData));
    }

    @DeleteMapping("/delete-post")
    public ResponseEntity<?> deletePost(@RequestParam("postId") long postId) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.deletePost(postId));
    }

    @PutMapping("/update-post")
    public ResponseEntity<?> updatePost(@RequestParam("postId") long postId, @RequestBody String newContent) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.updatePost(postId, newContent));
    }
    @GetMapping("/retrieve-friends-posts")
    public ResponseEntity<List<PostResponse>> retrieveFriendsPosts(@RequestParam("userId") long userId) {
        try {
            List<PostResponse> postResponses = postService.retrieveFriendsPosts(userId);
            return ResponseEntity.ok(postResponses);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/get-userId-by-postId")
    public ResponseEntity<Long> getUserIdByPostId(@RequestParam("postId") long postId) {
        try {
            Long userId = postService.getUserIdByPostId(postId);
            return ResponseEntity.ok(userId);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}


