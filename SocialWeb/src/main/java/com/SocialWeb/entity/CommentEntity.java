package com.SocialWeb.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;

@Builder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "web_comment")
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonBackReference("user-comments")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "post_id", referencedColumnName = "id")
    @JsonBackReference("post-comments")
    private PostEntity post;

    private Date createdAt;
    private Date updatedAt;
    private boolean isDeleted;
}
