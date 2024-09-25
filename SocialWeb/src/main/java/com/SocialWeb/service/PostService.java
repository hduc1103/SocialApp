package com.SocialWeb.service;

import com.SocialWeb.entity.Post;
import com.SocialWeb.entity.User;
import com.SocialWeb.repository.PostRepository;
import com.SocialWeb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.SocialWeb.Message.*;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Post> getPostsByUser(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        User user = userOptional.orElseThrow(() -> new RuntimeException(USER_NOT_FOUND + username));
        return postRepository.findByUser(user);
    }

    public String createPost(String username, String content) {
        User user = userRepository.findByUsername(username).orElseThrow();
        Post post = new Post();
        post.setUser(user);
        post.setContent(content);
        post.setCreatedAt(new Date());
        postRepository.save(post);
        return Y_POST;
    }

    public long numberOfLikes(long postId){
        return postRepository.LikeCount(postId);
    }

    public String updatePost(long postId, String newContent){
        Post post = postRepository.findById(postId).orElseThrow();

        post.setContent(newContent);
        post.setUpdateAt(new Date());
        postRepository.save(post);
        return U_POST;
    }

    public String deletePost(long postId){
        postRepository.deleteById(postId);
        return D_POST;
    }

}
