package com.SocialWeb.service.impl;

import com.SocialWeb.entity.*;
import com.SocialWeb.repository.SupportTicketRepository;
import com.SocialWeb.repository.TicketCommentRepository;
import com.SocialWeb.repository.UserRepository;
import com.SocialWeb.service.interfaces.SupportTicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.SocialWeb.Message.*;

@Service
public class SupportTicketServiceImpl implements SupportTicketService {

    @Autowired
    SupportTicketRepository supportTicketRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TicketCommentRepository ticketCommentRepository;

    @Override
    public void createTicket(SupportTicketEntity supportTicketEntity) {
        supportTicketRepository.save(supportTicketEntity);
    }

    @Override
    public SupportTicketEntity findSupportTicket(Long ticket_id) {
        return supportTicketRepository.findById(ticket_id).orElseThrow();
    }

    @Override
    public String updateTicket(Long userId, String content, Long id) {
        if (supportTicketRepository.checkUserTicket(userId) != id) {
            return DENIED_ACCESS_TICKET;
        }
        SupportTicketEntity supportTicketEntity = supportTicketRepository.findById(id).orElseThrow();
        supportTicketEntity.setContent(content);
        supportTicketRepository.save(supportTicketEntity);
        return U_SUPPORT_TICKET;
    }

    @Override
    public void addTicketComment(TicketCommentEntity ticketCommentEntity) {
        ticketCommentRepository.save(ticketCommentEntity);
    }

    @Override
    public void updateTicketComment(Long comment_id, String new_content) {
        TicketCommentEntity ticketCommentEntity = ticketCommentRepository.findById(comment_id).orElseThrow();
        ticketCommentEntity.setText(new_content);
        ticketCommentEntity.setUpdatedAt(new Date());
        ticketCommentRepository.save(ticketCommentEntity);
    }

    @Override
    public void deleteTicketComment(Long comment_id) {
        TicketCommentEntity ticketCommentEntity = ticketCommentRepository.findById(comment_id).orElseThrow();
        ticketCommentRepository.delete(ticketCommentEntity);
    }

    @Override
    public List<SupportTicketEntity> getAllTicketsByUserId(Long userId) {
        return supportTicketRepository.findByUserId(userId);
    }

    @Override
    public void deleteSupportTicket(Long ticketId) {
        supportTicketRepository.delete(supportTicketRepository.findById(ticketId).orElseThrow());
    }

    @Override
    public List<SupportTicketEntity> getAllSupportTickets() {
        return supportTicketRepository.findAll();
    }
}
