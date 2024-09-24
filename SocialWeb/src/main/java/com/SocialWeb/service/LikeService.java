package com.SocialWeb.service;

import com.SocialWeb.entity.Post;
import com.SocialWeb.entity.User;
import com.SocialWeb.repository.PostRepository;
import com.SocialWeb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.SocialWeb.config.Message.*;

@Service
public class LikeService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    public String likePost(String username, Long postId) {
        User user = userRepository.findByUsername(username).orElseThrow();
        int alreadyLiked = postRepository.checkUserLikedPost(user.getId(), postId);
        System.out.println("Already liked count: " + alreadyLiked);
        if (alreadyLiked != 0) {
            return Y_LIKE;
        }
        postRepository.addLike(user.getId(), postId);
        return LIKE;
    }
    public String dislikePost(String username, Long postId) {
        User user = userRepository.findByUsername(username).orElseThrow();
        long user_id = Math.toIntExact(user.getId());
        System.out.println(user_id);
        System.out.println(postId);
        int alreadyLiked = postRepository.checkUserLikedPost(user_id, postId);
        System.out.println("Already liked count: " + alreadyLiked);
        if (alreadyLiked == 0) {
            return N_LIKE;
        }
        postRepository.removeLike(user_id, postId);
        return DISLIKE;
    }


}
