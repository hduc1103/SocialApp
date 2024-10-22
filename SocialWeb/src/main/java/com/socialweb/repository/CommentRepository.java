package com.socialweb.repository;

import com.socialweb.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    @Query(value = "SELECT u.name FROM web_comment c JOIN web_user u ON c.user_id = u.id WHERE c.id = :commentId", nativeQuery = true)
    String getCommentUser(@Param("commentId") long commentId);

    @Query(value = "SELECT cmt.post_id FROM web_comment cmt WHERE id= :commentId", nativeQuery = true)
    Long getPostId(@Param("commentId") Long commentId);
}

