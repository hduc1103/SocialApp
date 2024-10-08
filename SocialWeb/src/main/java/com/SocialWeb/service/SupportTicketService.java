package com.SocialWeb.service;

import com.SocialWeb.entity.*;
import com.SocialWeb.repository.SupportTicketRepository;
import com.SocialWeb.repository.TicketCommentRepository;
import com.SocialWeb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.SocialWeb.Message.*;

@Service
public class SupportTicketService {
    @Autowired
    SupportTicketRepository supportTicketRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    TicketCommentRepository ticketCommentRepository;

    public void createTicket(SupportTicketEntity supportTicketEntity) {
        supportTicketRepository.save(supportTicketEntity);
    }

    public SupportTicketEntity findSupportTicket(Long ticket_id) {
        return supportTicketRepository.findById(ticket_id).orElseThrow();
    }

    public String updateTicket(Long userId, String content, Long id) {
        if (supportTicketRepository.checkUserTicket(userId) != id) {
            return DENIED_ACCESS_TICKET;
        }
        SupportTicketEntity supportTicketEntity = supportTicketRepository.findById(id).orElseThrow();
        supportTicketEntity.setContent(content);
        supportTicketRepository.save(supportTicketEntity);
        return U_SUPPORT_TICKET;
    }

    public void addTicketComment(TicketCommentEntity ticketCommentEntity) {
        ticketCommentRepository.save(ticketCommentEntity);
    }

    public void updateTicketComment(Long comment_id, String new_content) {
        TicketCommentEntity ticketCommentEntity = ticketCommentRepository.findById(comment_id).orElseThrow();
        ticketCommentEntity.setText(new_content);
        ticketCommentEntity.setUpdatedAt(new Date());
        ticketCommentRepository.save(ticketCommentEntity);
    }

    public void deleteTicketComment(Long comment_id) {
        TicketCommentEntity ticketCommentEntity = ticketCommentRepository.findById(comment_id).orElseThrow();
        ticketCommentRepository.delete(ticketCommentEntity);
    }

    public List<SupportTicketEntity> getAllTicketsByUserId(Long userId) {
        return supportTicketRepository.findByUserId(userId);
    }

    public void deleteSupportTicket(Long ticketId) {
        supportTicketRepository.delete(supportTicketRepository.findById(ticketId).orElseThrow());
    }

    public List<SupportTicketEntity> getAllSupportTickets() {
        return supportTicketRepository.findAll();
    }
}


