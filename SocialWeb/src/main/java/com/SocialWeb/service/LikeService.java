package com.SocialWeb.service;

import com.SocialWeb.entity.Like;
import com.SocialWeb.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    public Like createLike(Like like) {
        return likeRepository.save(like);
    }

    public Like getLikeById(int id) {
        Optional<Like> like = likeRepository.findById(id);
        return like.orElse(null);
    }

    public List<Like> getAllLikes() {
        return likeRepository.findAll();
    }

    public boolean deleteLike(int id) {
        if (likeRepository.existsById(id)) {
            likeRepository.deleteById(id);
            return true;
        }
        return false;
    }
}

