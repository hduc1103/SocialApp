package com.SocialWeb.repository;

import com.SocialWeb.entity.PostEntity;
import com.SocialWeb.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<PostEntity, Long> {
    List<PostEntity> findByUser(UserEntity userEntity);

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

    @Query("SELECT p FROM PostEntity p WHERE p.content LIKE %:content%")
    List<PostEntity> searchPostsByContent(@Param("content") String content);

    @Transactional
    @Query(value = "SELECT COUNT(post_id) FROM web_likes WHERE post_id = :postId", nativeQuery = true)
    long LikeCount(@Param("postId") long postId);

    @Transactional
    @Query(value = "SELECT wp.id as post_id, wp.content, wp.created_at, wp.updated_at, COUNT(wl.user_id) as like_count " +
            "FROM web_post wp " +
            "LEFT JOIN web_likes wl ON wp.id = wl.post_id " +
            "WHERE wp.user_id = (SELECT wu.id FROM web_user wu WHERE wu.username = :userName) " +
            "GROUP BY wp.id, wp.content, wp.created_at, wp.updated_at", nativeQuery = true)
    List<Object[]> getPostsWithLikeCountByUsername(@Param("userName") String userName);

    @Transactional
    @Query(value = """
                SELECT DISTINCT p.* FROM web_post p
                JOIN web_friends wf
                ON (wf.user_id1 = :userId AND wf.user_id2 = p.user_id)
                OR (wf.user_id2 = :userId AND wf.user_id1 = p.user_id)
                ORDER BY p.created_at DESC
                LIMIT 20
            """, nativeQuery = true)
    List<PostEntity> retrieveRecentFriendPosts(@Param("userId") Long userId);

}
