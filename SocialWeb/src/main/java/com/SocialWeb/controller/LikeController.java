package com.SocialWeb.controller;

import com.SocialWeb.entity.Like;
import com.SocialWeb.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/likes")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @PostMapping
    public ResponseEntity<Like> createLike(@RequestBody Like like) {
        Like createdLike = likeService.createLike(like);
        return ResponseEntity.ok(createdLike);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Like> getLikeById(@PathVariable int id) {
        Like like = likeService.getLikeById(id);
        return like != null ? ResponseEntity.ok(like) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<Like>> getAllLikes() {
        List<Like> likes = likeService.getAllLikes();
        return ResponseEntity.ok(likes);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLike(@PathVariable int id) {
        boolean isDeleted = likeService.deleteLike(id);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
