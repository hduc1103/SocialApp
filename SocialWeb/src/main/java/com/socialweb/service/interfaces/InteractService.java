package com.socialweb.service.interfaces;

import com.socialweb.domain.response.CommentResponse;

import java.util.Map;

public interface InteractService {

    CommentResponse addComment(Long postId, String token, Map<String, String> text);

    void updateComment(String token, Long commentId, Map<String, String> new_comment);

    String deleteComment(String token, Long cmtId);

    String likePost(String token, Long postId);

    String dislikePost(String token, Long postId);

    String getCommentAuthor(long commentId);

    Long getPostIdOfComment(Long commentId);
}
