package com.SocialWeb.service.interfaces;

import com.SocialWeb.domain.response.CommentResponse;

import java.util.Map;

public interface InteractService {

    CommentResponse addComment(Long postId, String token, Map<String, String> text);

    void updateComment(Long commentId, Map<String, String> new_comment);

    String deleteComment(Long cmtId);

    String likePost(String token, Long postId);

    String dislikePost(String token, Long postId);

    String getCommentAuthor(long commentId);
}
