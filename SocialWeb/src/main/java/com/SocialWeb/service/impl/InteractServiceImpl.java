package com.SocialWeb.service.impl;

import com.SocialWeb.domain.response.CommentResponse;
import com.SocialWeb.entity.CommentEntity;
import com.SocialWeb.entity.PostEntity;
import com.SocialWeb.entity.UserEntity;
import com.SocialWeb.repository.CommentRepository;
import com.SocialWeb.repository.PostRepository;
import com.SocialWeb.repository.UserRepository;
import com.SocialWeb.security.JwtUtil;
import com.SocialWeb.service.interfaces.InteractService;
import com.SocialWeb.service.interfaces.NotificationService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

import static com.SocialWeb.Message.*;

@Service
public class InteractServiceImpl implements InteractService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final JwtUtil jwtUtil;
    private final NotificationService notificationService;

    public InteractServiceImpl(UserRepository userRepository, PostRepository postRepository, CommentRepository commentRepository, JwtUtil jwtUtil, NotificationService notificationService) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.jwtUtil = jwtUtil;
        this.notificationService = notificationService;
    }

    private String extractUsername(String token) {
        String jwtToken = token.substring(7);
        return jwtUtil.extractUsername(jwtToken);
    }

    @Override
    public CommentResponse addComment(Long postId, String token, Map<String, String> text) {
        String username = extractUsername(token);
        String content = text.get("text");
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow();
        PostEntity postEntity = postRepository.findById(postId).orElseThrow();
        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setUser(userEntity);
        commentEntity.setPost(postEntity);
        commentEntity.setText(content);
        commentEntity.setCreatedAt(new Date());
        commentEntity.setUpdatedAt(new Date());
        commentEntity.setDeleted(false);
        commentRepository.save(commentEntity);

        UserEntity userEntity1= userRepository.findById(postRepository.getUserOfPost(postId)).orElseThrow();
        if (!Objects.equals(userEntity1.getId(), userEntity.getId())) {
            String notification = username + NOTI_CMT;
            notificationService.sendNotification(userEntity1, notification);
        }
        return CommentResponse.builder()
                .id(commentEntity.getId())
                .user_id(userEntity.getId())
                .text(commentEntity.getText())
                .createdAt(commentEntity.getCreatedAt())
                .updatedAt(commentEntity.getUpdatedAt())
                .build();
    }

    @Override
    public void updateComment(String token, Long commentId, Map<String, String> new_comment) {
        String username = extractUsername(token);
        String new_content = new_comment.get("text");
        CommentEntity commentEntity = commentRepository.findById(commentId).orElseThrow();
        commentEntity.setText(new_content);
        commentEntity.setUpdatedAt(new Date());
        commentRepository.save(commentEntity);

        Long postId = commentRepository.getPostId(commentId);
        UserEntity userEntity1= userRepository.findById(postRepository.getUserOfPost(postId)).orElseThrow();
        String notification = username + NOTI_CMT;
        notificationService.sendNotification(userEntity1,notification);
    }

    @Override
    public String deleteComment(Long cmtId) {
        try {
            CommentEntity commentEntity = commentRepository.findById(cmtId).orElseThrow();
            commentEntity.setDeleted(true);
            commentRepository.save(commentEntity);
            return CMT_DEL;
        } catch (NoSuchElementException e) {
            System.err.println(ERROR_MSG + e.getMessage());
            return ERROR_MSG + e.getMessage();
        } catch (Exception e) {
            System.err.println(UNEXPECTED_ERROR + e.getMessage());
            return UNEXPECTED_ERROR + e.getMessage();
        }
    }

    @Override
    public String likePost(String token, Long postId) {
        String username = extractUsername(token);
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow();
        int alreadyLiked = postRepository.checkUserLikedPost(userEntity.getId(), postId);
        if (alreadyLiked != 0) {
            return Y_LIKE;
        }
        postRepository.addLike(userEntity.getId(), postId);
        UserEntity userEntity1= userRepository.findById(postRepository.getUserOfPost(postId)).orElseThrow();
        if (!Objects.equals(userEntity1.getId(), userEntity.getId())){
        String notification = username + NOTI_LIKE;
        notificationService.sendNotification(userEntity1,notification);
        }
        return LIKE;
    }

    @Override
    public String dislikePost(String token, Long postId) {
        String username = extractUsername(token);
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow();
        long userId = Math.toIntExact(userEntity.getId());
        int alreadyLiked = postRepository.checkUserLikedPost(userId, postId);
        if (alreadyLiked == 0) {
            return N_LIKE;
        }
        postRepository.removeLike(userId, postId);
        return DISLIKE;
    }

    @Override
    public String getCommentAuthor(long commentId) {
        return commentRepository.getCommentUser(commentId);
    }
}
