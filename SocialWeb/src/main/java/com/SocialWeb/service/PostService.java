package com.SocialWeb.service;

import com.SocialWeb.entity.PostEntity;
import com.SocialWeb.entity.UserEntity;
import com.SocialWeb.repository.PostRepository;
import com.SocialWeb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.SocialWeb.Message.*;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    public List<PostEntity> getPostsByUser(String username) {
        UserEntity userEntity =userRepository.findByUsername(username).orElseThrow(()-> new RuntimeException(USER_NOT_FOUND + username));
        return postRepository.findByUser(userEntity);
    }

    public String createPost(String username, String content) {
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow();
        PostEntity postEntity = new PostEntity();
        postEntity.setUser(userEntity);
        postEntity.setContent(content);
        postEntity.setCreatedAt(new Date());
        postRepository.save(postEntity);
        return Y_POST;
    }

    public long numberOfLikes(long postId){
        return postRepository.LikeCount(postId);
    }

    public String updatePost(long postId, String newContent){
        PostEntity postEntity = postRepository.findById(postId).orElseThrow();

        postEntity.setContent(newContent);
        postEntity.setUpdatedAt(new Date());
        postRepository.save(postEntity);
        return U_POST;
    }

    public String deletePost(long postId){
        postRepository.deleteById(postId);
        return D_POST;
    }

    public List<PostEntity> searchPostsByKeyWord(String keyword){
        return postRepository.searchPostsByContent(keyword);
    }
}
