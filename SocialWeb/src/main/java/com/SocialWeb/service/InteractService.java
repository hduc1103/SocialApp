package com.SocialWeb.service;

import com.SocialWeb.entity.CommentEntity;
import com.SocialWeb.entity.PostEntity;
import com.SocialWeb.entity.UserEntity;
import com.SocialWeb.repository.CommentRepository;
import com.SocialWeb.repository.PostRepository;
import com.SocialWeb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.NoSuchElementException;

import static com.SocialWeb.Message.*;

@Service
public class InteractService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    CommentRepository commentRepository;

    public String addComment(Long postId, String username, String text) {
        try {
            UserEntity userEntity = userRepository.findByUsername(username).orElseThrow();
            PostEntity postEntity = postRepository.findById(postId).orElseThrow();
            CommentEntity commentEntity = new CommentEntity();
            commentEntity.setUser(userEntity);
            commentEntity.setPost(postEntity);
            commentEntity.setText(text);
            commentEntity.setCreatedAt(new Date());
            commentRepository.save(commentEntity);
            return CMT_ADD;
        } catch (NoSuchElementException e) {
            System.err.println(ERROR_MSG + e.getMessage());
            return ERROR_MSG + e.getMessage();
        } catch (Exception e) {
            System.err.println(UNEXPECTED_ERROR + e.getMessage());
            return UNEXPECTED_ERROR + e.getMessage();
        }
    }
    public void updateComment(Long commentId, String new_comment){
        CommentEntity commentEntity = commentRepository.findById(commentId).orElseThrow();

        commentEntity.setText(new_comment);
        commentEntity.setUpdatedAt(new Date());
        commentRepository.save(commentEntity);
    }
    public String deleteComment(Long postId, String username, Long cmtId) {
        try {
            UserEntity userEntity = userRepository.findByUsername(username).orElseThrow();
            PostEntity postEntity = postRepository.findById(postId).orElseThrow();
            CommentEntity commentEntity = commentRepository.findById(cmtId).orElseThrow();

            commentRepository.deleteById(cmtId);
            return CMT_DEL;
        } catch (NoSuchElementException e) {
            System.err.println(ERROR_MSG + e.getMessage());
            return ERROR_MSG + e.getMessage();
        } catch (Exception e) {
            System.err.println(UNEXPECTED_ERROR + e.getMessage());
            return UNEXPECTED_ERROR + e.getMessage();
        }
    }
    public String likePost(String username, Long postId) {
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow();
        int alreadyLiked = postRepository.checkUserLikedPost(userEntity.getId(), postId);
        if (alreadyLiked != 0) {
            return Y_LIKE;
        }
        postRepository.addLike(userEntity.getId(), postId);
        return LIKE;
    }

    public String dislikePost(String username, Long postId) {
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow();
        long user_id = Math.toIntExact(userEntity.getId());
        int alreadyLiked = postRepository.checkUserLikedPost(user_id, postId);
        if (alreadyLiked == 0) {
            return N_LIKE;
        }
        postRepository.removeLike(user_id, postId);
        return DISLIKE;
    }
}
