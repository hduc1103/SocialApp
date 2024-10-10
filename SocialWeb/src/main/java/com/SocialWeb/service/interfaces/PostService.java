package com.SocialWeb.service.interfaces;

import com.SocialWeb.domain.response.PostResponse;
import com.SocialWeb.entity.PostEntity;
import java.util.List;

public interface PostService {

    List<PostEntity> getPostsByUser(long userId);

    PostEntity getPostById(long postId);

    PostResponse createPost(String username, String content);

    long numberOfLikes(long postId);

    String updatePost(long postId, String newContent);

    String deletePost(long postId);

    List<PostEntity> searchPostsByKeyWord(String keyword);

    List<PostEntity> retrieveRecentFriendPosts(Long userId);
}
