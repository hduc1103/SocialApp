package com.SocialWeb.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@DiscriminatorValue("UserEntity")
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({ "friends" })
@Table(name = "web_user")
public class UserEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String username;
        private String password;
        private String email;

        @ManyToMany
        @JoinTable(name = "web_friends", joinColumns = @JoinColumn(name = "user_id1"), inverseJoinColumns = @JoinColumn(name = "user_id2"))
        @JsonIgnoreProperties("friends")
        @JsonIgnore
        private List<UserEntity> friends;

        @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
        @JsonManagedReference("user-posts")
        private List<PostEntity> posts;

        @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
        @JsonManagedReference("user-comments")
        private List<CommentEntity> comments;

        @ManyToMany
        @JsonIgnore
        @JoinTable(name = "web_likes", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "post_id"))
        private List<PostEntity> likedPosts;
}
