package com.SocialWeb.service.impl;

import com.SocialWeb.entity.CommentEntity;
import com.SocialWeb.entity.PostEntity;
import com.SocialWeb.entity.UserEntity;
import com.SocialWeb.repository.CommentRepository;
import com.SocialWeb.repository.PostRepository;
import com.SocialWeb.repository.UserRepository;
import com.SocialWeb.service.interfaces.InteractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.NoSuchElementException;

import static com.SocialWeb.Message.*;

@Service
public class InteractServiceImpl implements InteractService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public String addComment(Long postId, String username, String text) {
        try {
            UserEntity userEntity = userRepository.findByUsername(username).orElseThrow();
            PostEntity postEntity = postRepository.findById(postId).orElseThrow();
            CommentEntity commentEntity = new CommentEntity();
            commentEntity.setUser(userEntity);
            commentEntity.setPost(postEntity);
            commentEntity.setText(text);
            commentEntity.setCreatedAt(new Date());
            commentEntity.setUpdatedAt(new Date());
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

    @Override
    public void updateComment(Long commentId, String new_comment) {
        CommentEntity commentEntity = commentRepository.findById(commentId).orElseThrow();
        commentEntity.setText(new_comment);
        commentEntity.setUpdatedAt(new Date());
        commentRepository.save(commentEntity);
    }

    @Override
    public String deleteComment(Long cmtId) {
        try {
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

    @Override
    public String likePost(String username, Long postId) {
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow();
        int alreadyLiked = postRepository.checkUserLikedPost(userEntity.getId(), postId);
        if (alreadyLiked != 0) {
            return Y_LIKE;
        }
        postRepository.addLike(userEntity.getId(), postId);
        return LIKE;
    }

    @Override
    public String dislikePost(String username, Long postId) {
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
