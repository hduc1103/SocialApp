package com.SocialWeb.controller;

import com.SocialWeb.entity.Comment;
import com.SocialWeb.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/comment")
public class CommentController {

    @Autowired
    CommentService commentService;

    @PostMapping
    public ResponseEntity<Comment> createComment(@RequestBody Comment comment){
        Comment createdComment = commentService.createComment(comment);
        return  ResponseEntity.ok(createdComment);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable int id){
        Comment comment = commentService.getCommentById(id);
        return comment != null ? ResponseEntity.ok(comment):ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comment> updateCommentById(@PathVariable int id, @RequestBody Comment comment){
        Comment updatedComment = commentService.updateComment(id, comment);
        return updatedComment != null ? ResponseEntity.ok(updatedComment) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Comment> deleteCommentById(@PathVariable int id){
        boolean isDeleted = commentService.deleteComment(id);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
