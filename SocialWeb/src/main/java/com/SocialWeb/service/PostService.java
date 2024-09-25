package com.SocialWeb.service;

import com.SocialWeb.dto.PostDTO;
import com.SocialWeb.entity.Post;
import com.SocialWeb.entity.User;
import com.SocialWeb.repository.PostRepository;
import com.SocialWeb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.SocialWeb.config.Message.*;

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

    public List<PostDTO> getPostsWithLikeCount(String username) {
        List<Object[]> results = postRepository.getPostsWithLikeCountByUsername(username);

        List<PostDTO> postDTOs = new ArrayList<>();
        for (Object[] result : results) {
            PostDTO postDTO = new PostDTO();
            postDTO.setId((Long) result[0]);
            postDTO.setContent((String) result[1]);
            postDTO.setCreatedAt((Timestamp) result[2]);
            postDTO.setUpdatedAt((Timestamp) result[3]);
            postDTO.setLikeCount(((Number) result[4]).intValue());
            postDTOs.add(postDTO);
        }
        return postDTOs;
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
}
