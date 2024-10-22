package com.socialweb.entity;

import com.socialweb.config.TicketStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "ticket_support")
public class SupportTicketEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;
    private Date createdAt;
    private Date endAt;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @OneToMany(mappedBy = "supportTicketEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("support_ticket-ticket_comments")
    private List<TicketCommentEntity> ticketCommentEntities;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    List<String> assignedUsers= new ArrayList<>();
}
