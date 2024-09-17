package com.SocialWeb.service;

import com.SocialWeb.repository.CommentRepository;
import com.SocialWeb.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    @Autowired
    CommentRepository commentRepository;

    public Comment createComment(Comment comment){
        return commentRepository.save(comment);
    }
    public Comment getCommentById(int id){
        Optional<Comment> comment = commentRepository.findById(id);
        return comment.orElse(null);
    }

    public List<Comment> getAllComments(){
        return commentRepository.findAll();
    }

    public Comment updateComment(int id, Comment comment){
        if(commentRepository.existsById((id))){
            comment.setId(id);
            return commentRepository.save(comment);
        }
        return null;
    }
    public boolean deleteComment(int id){
        if(commentRepository.existsById(id)){
            commentRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
