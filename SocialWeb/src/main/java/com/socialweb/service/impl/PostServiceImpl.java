package com.socialweb.service.impl;

import com.socialweb.domain.response.CommentResponse;
import com.socialweb.domain.response.PostResponse;
import com.socialweb.entity.PostEntity;
import com.socialweb.entity.UserEntity;
import com.socialweb.repository.PostRepository;
import com.socialweb.repository.UserRepository;
import com.socialweb.security.JwtUtil;
import com.socialweb.service.interfaces.NotificationService;
import com.socialweb.service.interfaces.PostService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.socialweb.Message.*;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final JwtUtil jwtUtil;

    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository, NotificationService notificationService, JwtUtil jwtUtil) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.jwtUtil = jwtUtil;
    }

    private String extractUsername(String token) {
        String jwtToken = token.substring(7);
        return jwtUtil.extractUsername(jwtToken);
    }


    //overflown
    @Override
    public List<PostResponse> getUserPosts(long userId) {
        List<PostEntity> postEntities = postRepository.findByUserAndNotDeleted(userId);
        return postEntities.stream()
                .map(postEntity -> PostResponse.builder()
                        .id(postEntity.getId())
                        .content(postEntity.getContent())
                        .createdAt(postEntity.getCreatedAt())
                        .updatedAt(postEntity.getUpdatedAt())
                        .userId(postEntity.getUser().getId())
                        .author(postEntity.getUser().getName())
                        .imgUrl(postEntity.getUser().getImg_url())
                        .comments(postEntity.getComments().stream()
                                .filter(commentEntity -> !commentEntity.isDeleted())
                                .map(commentEntity -> CommentResponse.builder()
                                        .id(commentEntity.getId())
                                        .user_id(commentEntity.getUser().getId())
                                        .text(commentEntity.getText())
                                        .createdAt(commentEntity.getCreatedAt())
                                        .updatedAt(commentEntity.getUpdatedAt())
                                        .author(commentEntity.getUser().getName())
                                        .imgUrl(commentEntity.getUser().getImg_url())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<PostResponse> admin_getUserPosts(long userId) {
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
                        .author(postEntity.getUser().getName())
                        .imgUrl(postEntity.getUser().getImg_url())
                        .isDeleted(postEntity.isDeleted())
                        .comments(postEntity.getComments().stream()
                                .map(commentEntity -> CommentResponse.builder()
                                        .id(commentEntity.getId())
                                        .user_id(commentEntity.getUser().getId())
                                        .text(commentEntity.getText())
                                        .createdAt(commentEntity.getCreatedAt())
                                        .updatedAt(commentEntity.getUpdatedAt())
                                        .isDeleted(commentEntity.isDeleted())
                                        .author(commentEntity.getUser().getName())
                                        .imgUrl(commentEntity.getUser().getImg_url())
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
        postEntity.setDeleted(false);
        postRepository.save(postEntity);
        return new PostResponse(
                postEntity.getId(),
                postEntity.getContent(),
                Collections.emptyList(),
                postEntity.getCreatedAt(),
                postEntity.getUpdatedAt(),
                userEntity.getId(),
                userEntity.getName(),
                userEntity.getImg_url()
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
        PostEntity postEntity = postRepository.findById(postId).orElseThrow();
        postEntity.setDeleted(true);
        notificationService.deleteAllPostOrCommentNoti(postId);
        postRepository.save(postEntity);
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
                        .author(postEntity.getUser().getName())
                        .imgUrl(postEntity.getUser().getImg_url())
                        .comments(postEntity.getComments().stream()
                                .map(commentEntity -> CommentResponse.builder()
                                        .id(commentEntity.getId())
                                        .user_id(commentEntity.getUser().getId())
                                        .text(commentEntity.getText())
                                        .createdAt(commentEntity.getCreatedAt())
                                        .updatedAt(commentEntity.getUpdatedAt())
                                        .isDeleted(commentEntity.isDeleted())
                                        .author(commentEntity.getUser().getName())
                                        .imgUrl(commentEntity.getUser().getImg_url())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAllUserPost(long userId) {
        postRepository.deleteAllUserPost(userId);
    }

    @Override
    public Long getUserIdByPostId(long postId) {
        return postRepository.findById(postId).orElseThrow().getUser().getId();
    }
}
