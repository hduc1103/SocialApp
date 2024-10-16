package com.SocialWeb.service.interfaces;

import com.SocialWeb.domain.response.PostResponse;
import com.SocialWeb.entity.PostEntity;
import java.util.List;
import java.util.Map;

public interface PostService {

    List<PostResponse> getUserPosts(long userId);

    List<PostResponse> admin_getUserPosts(long userId);

    PostEntity getPostById(long postId);

    PostResponse createPost(String token, Map<String, String> postData);

    long numberOfLikes(long postId);

    String updatePost(long postId, String newContent);

    String deletePost(long postId);

    List<PostEntity> searchPostsByKeyWord(String keyword);

    List<PostResponse> retrieveFriendsPosts(Long userId);
}
