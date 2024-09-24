package com.SocialWeb.repository;

import com.SocialWeb.entity.Post;
import com.SocialWeb.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUser(User user);
    @Query(value = "SELECT COUNT(*) > 0 FROM web_likes WHERE user_id = :userId AND post_id = :postId", nativeQuery = true)
    int checkUserLikedPost(@Param("userId") Long userId, @Param("postId") Long postId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO web_likes (user_id, post_id) VALUES (:userId, :postId)", nativeQuery = true)
    void addLike(@Param("userId") Long userId, @Param("postId") Long postId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM web_likes WHERE user_id = :userId AND post_id = :postId", nativeQuery = true)
    void removeLike(@Param("userId") Long userId, @Param("postId") Long postId);

    @Query("SELECT p FROM Post p WHERE p.content LIKE %:keyword%")
    List<Post> searchPostsByContent(@Param("keyword") String keyword);
}
