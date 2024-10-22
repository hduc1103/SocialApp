package com.socialweb.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "web_friends")
public class WebFriendEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id1", nullable = false)
    private Long userId1;

    @Column(name = "user_id2", nullable = false)
    private Long userId2;

    @Column(name = "user1_accepted")
    private Long user1Accepted;

    @Column(name = "user2_accepted")
    private Long user2Accepted;
}

