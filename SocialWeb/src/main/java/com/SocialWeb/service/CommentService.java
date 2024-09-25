package com.SocialWeb.service;

import com.SocialWeb.entity.Comment;
import com.SocialWeb.entity.Post;
import com.SocialWeb.entity.User;
import com.SocialWeb.repository.CommentRepository;
import com.SocialWeb.repository.PostRepository;
import com.SocialWeb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.NoSuchElementException;

import static com.SocialWeb.config.Message.*;

@Service
public class CommentService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    CommentRepository commentRepository;

    public String addComment(Long postId, String username, String text) {
        System.out.println("Su dung add comment");
        try {
            User user = userRepository.findByUsername(username).orElseThrow();
            Post post = postRepository.findById(postId).orElseThrow();
            Comment comment = new Comment();
            comment.setUser(user);
            comment.setPost(post);
            comment.setText(text);
            comment.setCreatedAt(new Date());
            commentRepository.save(comment);
            return CMT_ADD;
        } catch (NoSuchElementException e) {
            System.err.println(ERROR_MSG + e.getMessage());
            return ERROR_MSG + e.getMessage();
        } catch (Exception e) {
            System.err.println(UNEXPECTED_ERROR + e.getMessage());
            return UNEXPECTED_ERROR + e.getMessage();
        }
    }
    public String deleteComment(Long postId, String username, Long cmtId) {
        System.out.println("Su dung delete cmt");
        try {
            User user = userRepository.findByUsername(username).orElseThrow();
            Post post = postRepository.findById(postId).orElseThrow();
            Comment comment = commentRepository.findById(cmtId).orElseThrow();

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
}
