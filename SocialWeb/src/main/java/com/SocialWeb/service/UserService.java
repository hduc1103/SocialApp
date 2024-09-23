package com.SocialWeb.service;

import com.SocialWeb.entity.*;
import com.SocialWeb.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

        return "Post liked successfully";
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void createUser(User user) {
        userRepository.save(user); // Save the user entity to the database
    }
}
