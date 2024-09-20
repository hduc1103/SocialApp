package com.SocialWeb.repository;

import com.SocialWeb.entity.Post;
import com.SocialWeb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUser(User user);
}
