package com.SocialWeb.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Like")
@Data
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Column(name = "created_at", nullable = false)
    private java.time.LocalDateTime createdAt;
}
