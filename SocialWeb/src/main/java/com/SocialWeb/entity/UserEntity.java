package com.SocialWeb.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "web_user")
public class UserEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String username;
        private String password;
        private String email;
        private String img_url;
        private String bio;
        private String address;

        @ManyToMany
        @JoinTable(name = "web_friends", joinColumns = @JoinColumn(name = "user_id1"), inverseJoinColumns = @JoinColumn(name = "user_id2"))
        @JsonIgnore
        private List<UserEntity> friends;

        @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
        @JsonManagedReference("user-posts")
        private List<PostEntity> posts;

        @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
        @JsonManagedReference("user-comments")
        private List<CommentEntity> comments;

        @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
        @JsonManagedReference("user-ticket_comments")
        private List<TicketCommentEntity> ticketComments;

        @ManyToMany
        @JsonIgnore
        @JoinTable(name = "web_likes", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "post_id"))
        private List<PostEntity> likedPosts;

        @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
        private List<SupportTicketEntity> supportTickets;

        @ElementCollection(fetch = FetchType.EAGER)
        @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
        @Column(name = "role")
        private List<String> roles;
}
