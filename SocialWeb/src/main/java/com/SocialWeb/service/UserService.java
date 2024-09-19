package com.SocialWeb.service;

import com.SocialWeb.entity.*;
import com.SocialWeb.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    public String addFriend(Long userId, Long friendId) {
        User user = userRepository.findById(userId).orElseThrow();
        User friend = userRepository.findById(friendId).orElseThrow();
        user.getFriends().add(friend);
        userRepository.save(user);
        return "Friend added successfully";
    }

    public String createPost(Long userId, String content) {
        User user = userRepository.findById(userId).orElseThrow();
        Post post = new Post(null, content, user, null);
        postRepository.save(post);
        return "Post created successfully";
    }

    public String addComment(Long postId, Long userId, String text) {
        User user = userRepository.findById(userId).orElseThrow();
        Post post = postRepository.findById(postId).orElseThrow();
        Comment comment = new Comment(null, text, user, post);
        commentRepository.save(comment);
        return "Comment added successfully";
    }

    public String likePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();
        // Implement logic to add a like (could involve adding a Like entity, if required)
        return "Post liked successfully";
    }
}
