package com.SocialWeb.service;

import com.SocialWeb.domain.response.PostResponse;
import com.SocialWeb.entity.PostEntity;
import com.SocialWeb.entity.UserEntity;
import com.SocialWeb.repository.PostRepository;
import com.SocialWeb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
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

    public List<PostEntity> getPostsByUser(long userId) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new RuntimeException(USER_NOT_FOUND + userId));
        return postRepository.findByUser(userEntity);
    }

    public PostEntity getPostById(long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new RuntimeException(POST_NOT_FOUND));
    }

    public PostResponse createPost(String username, String content) {
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow();
        PostEntity postEntity = new PostEntity();
        postEntity.setUser(userEntity);
        postEntity.setContent(content);
        postEntity.setCreatedAt(new Date());
        postEntity.setUpdatedAt(new Date());
        postRepository.save(postEntity);
        return new PostResponse(
                postEntity.getId(),
                postEntity.getContent(),
                Collections.emptyList(),
                postEntity.getCreatedAt(),
                postEntity.getUpdatedAt(),
                userEntity.getId()
        );
    }


    public long numberOfLikes(long postId) {
        return postRepository.LikeCount(postId);
    }

    public String updatePost(long postId, String newContent) {
        PostEntity postEntity = postRepository.findById(postId).orElseThrow();
        newContent = newContent.replaceAll("\"", "");
        postEntity.setContent(newContent);
        postEntity.setUpdatedAt(new Date());
        postRepository.save(postEntity);
        return U_POST;
    }

    public String deletePost(long postId) {
        postRepository.deleteById(postId);
        return D_POST;
    }

    public List<PostEntity> searchPostsByKeyWord(String keyword) {
        return postRepository.searchPostsByContent(keyword);
    }

    public List<PostEntity> retrieveRecentFriendPosts(Long userId) {
        return postRepository.retrieveRecentFriendPosts(userId);
    }
}
