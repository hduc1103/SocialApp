package com.socialweb.repository;

import com.socialweb.entity.PostEntity;
import com.socialweb.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<PostEntity, Long> {
    List<PostEntity> findByUser(UserEntity userEntity);

    @Query(value = "SELECT * FROM web_post WHERE user_id = :userId AND is_deleted = 0", nativeQuery = true)
    List<PostEntity> findByUserAndNotDeleted(@Param("userId") Long userId);

    @Query(value = "SELECT user_id FROM web_post WHERE id = :postId", nativeQuery = true)
    Long getUserOfPost(@Param("postId")Long postId);

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

    @Query("SELECT p FROM PostEntity p WHERE p.content LIKE %:content% AND p.isDeleted = false")
    List<PostEntity> searchPostsByContent(@Param("content") String content);

    @Transactional
    @Query(value = "SELECT COUNT(post_id) FROM web_likes WHERE post_id = :postId AND is_deleted = 0", nativeQuery = true)
    long LikeCount(@Param("postId") long postId);

    @Transactional
    @Query(value = """
                SELECT DISTINCT p.* FROM web_post p
                JOIN web_friends wf
                ON (wf.user_id1 = :userId AND wf.user_id2 = p.user_id)
                OR (wf.user_id2 = :userId AND wf.user_id1 = p.user_id)
                WHERE p.is_deleted=0
                ORDER BY p.created_at DESC
                LIMIT 20
            """, nativeQuery = true)
    List<PostEntity> retrieveRecentFriendPosts(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE web_post SET is_deleted = 1 WHERE user_id = :userId", nativeQuery = true)
    void deleteAllUserPost(@Param("userId") Long userId);
}
