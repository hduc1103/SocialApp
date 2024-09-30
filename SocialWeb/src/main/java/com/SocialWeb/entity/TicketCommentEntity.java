package com.SocialWeb.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ticket_comment")
public class TicketCommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonBackReference("user-ticket_comments")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "ticket_id", referencedColumnName = "id")
    @JsonBackReference("support_ticket-ticket_comments")
    private SupportTicketEntity supportTicketEntity;

    private Date createdAt;
    private Date updatedAt;
}
