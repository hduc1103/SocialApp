package com.SocialWeb.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Data
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "status")
@DiscriminatorValue("In Progress")
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ticket_support")
public class SupportTicketEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private List<String> content;
    private Date createdAt;
    private Date endAt;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}
