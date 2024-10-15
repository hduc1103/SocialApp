package com.SocialWeb.service.impl;

import com.SocialWeb.domain.response.CommentResponse;
import com.SocialWeb.domain.response.PostResponse;
import com.SocialWeb.entity.PostEntity;
import com.SocialWeb.entity.UserEntity;
import com.SocialWeb.repository.PostRepository;
import com.SocialWeb.repository.UserRepository;
import com.SocialWeb.security.JwtUtil;
import com.SocialWeb.service.interfaces.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.SocialWeb.Message.*;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository, JwtUtil jwtUtil) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    private String extractUsername(String token) {
        String jwtToken = token.substring(7);
        return jwtUtil.extractUsername(jwtToken);
    }

    @Override
    public List<PostResponse> getUserPosts(long userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND + userId));

        List<PostEntity> postEntities = postRepository.findByUser(userEntity);

        return postEntities.stream()
                .map(postEntity -> PostResponse.builder()
                        .id(postEntity.getId())
                        .content(postEntity.getContent())
                        .createdAt(postEntity.getCreatedAt())
                        .updatedAt(postEntity.getUpdatedAt())
                        .userId(postEntity.getUser().getId())
                        .comments(postEntity.getComments().stream()
                                .map(commentEntity -> CommentResponse.builder()
                                        .id(commentEntity.getId())
                                        .user_id(commentEntity.getUser().getId())
                                        .text(commentEntity.getText())
                                        .createdAt(commentEntity.getCreatedAt())
                                        .updatedAt(commentEntity.getUpdatedAt())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public PostEntity getPostById(long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new RuntimeException(POST_NOT_FOUND));
    }

    @Override
    public PostResponse createPost(String token, Map<String, String> postData) {
        String username = extractUsername(token);
        String content = postData.get("content");
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
    public List<PostResponse> retrieveFriendsPosts(Long userId) {
        List<PostEntity> postEntities = postRepository.retrieveRecentFriendPosts(userId);

        return postEntities.stream()
                .map(postEntity -> PostResponse.builder()
                        .id(postEntity.getId())
                        .content(postEntity.getContent())
                        .createdAt(postEntity.getCreatedAt())
                        .updatedAt(postEntity.getUpdatedAt())
                        .userId(postEntity.getUser().getId())
                        .comments(postEntity.getComments().stream()
                                .map(commentEntity -> CommentResponse.builder()
                                        .id(commentEntity.getId())
                                        .user_id(commentEntity.getUser().getId())
                                        .text(commentEntity.getText())
                                        .createdAt(commentEntity.getCreatedAt())
                                        .updatedAt(commentEntity.getUpdatedAt())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

}
