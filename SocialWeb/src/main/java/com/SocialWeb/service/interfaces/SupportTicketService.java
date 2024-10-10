package com.SocialWeb.service.interfaces;

import com.SocialWeb.entity.SupportTicketEntity;
import com.SocialWeb.entity.TicketCommentEntity;
import java.util.List;

public interface SupportTicketService {

    void createTicket(SupportTicketEntity supportTicketEntity);

    SupportTicketEntity findSupportTicket(Long ticket_id);

    String updateTicket(Long userId, String content, Long id);

    void addTicketComment(TicketCommentEntity ticketCommentEntity);

    void updateTicketComment(Long comment_id, String new_content);

    void deleteTicketComment(Long comment_id);

    List<SupportTicketEntity> getAllTicketsByUserId(Long userId);

    void deleteSupportTicket(Long ticketId);

    List<SupportTicketEntity> getAllSupportTickets();
}
