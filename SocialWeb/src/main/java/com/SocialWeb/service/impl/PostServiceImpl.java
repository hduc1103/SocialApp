package com.SocialWeb.service.impl;

import com.SocialWeb.domain.response.PostResponse;
import com.SocialWeb.entity.PostEntity;
import com.SocialWeb.entity.UserEntity;
import com.SocialWeb.repository.PostRepository;
import com.SocialWeb.repository.UserRepository;
import com.SocialWeb.service.interfaces.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.SocialWeb.Message.*;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public List<PostEntity> getPostsByUser(long userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND + userId));
        return postRepository.findByUser(userEntity);
    }

    @Override
    public PostEntity getPostById(long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new RuntimeException(POST_NOT_FOUND));
    }

    @Override
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

    @Override
    public long numberOfLikes(long postId) {
        return postRepository.LikeCount(postId);
    }

    @Override
    public String updatePost(long postId, String newContent) {
        PostEntity postEntity = postRepository.findById(postId).orElseThrow();
        newContent = newContent.replaceAll("\"", "");
        postEntity.setContent(newContent);
        postEntity.setUpdatedAt(new Date());
        postRepository.save(postEntity);
        return U_POST;
    }

    @Override
    public String deletePost(long postId) {
        postRepository.deleteById(postId);
        return D_POST;
    }

    @Override
    public List<PostEntity> searchPostsByKeyWord(String keyword) {
        return postRepository.searchPostsByContent(keyword);
    }

    @Override
    public List<PostEntity> retrieveRecentFriendPosts(Long userId) {
        return postRepository.retrieveRecentFriendPosts(userId);
    }
}
