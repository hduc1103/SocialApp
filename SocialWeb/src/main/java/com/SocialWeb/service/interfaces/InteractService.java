package com.SocialWeb.service.interfaces;

public interface InteractService {

    String addComment(Long postId, String username, String text);

    void updateComment(Long commentId, String new_comment);

    String deleteComment(Long cmtId);

    String likePost(String username, Long postId);

    String dislikePost(String username, Long postId);

    String getCommentAuthor(long commentId);
}
