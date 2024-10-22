package com.socialweb.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Builder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "web_post")
public class PostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonBackReference("user-posts")
    private UserEntity user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    @JsonManagedReference("post-comments")
    private List<CommentEntity> comments;

    @JsonIgnore
    @ManyToMany(mappedBy = "likedPosts")
    private List<UserEntity> likedByUsers;

    private Date createdAt;
    private Date updatedAt;
    private boolean isDeleted;
}
