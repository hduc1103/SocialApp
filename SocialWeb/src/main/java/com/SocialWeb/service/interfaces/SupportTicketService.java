package com.SocialWeb.service.interfaces;

import com.SocialWeb.domain.response.SupportTicketResponse;
import com.SocialWeb.entity.SupportTicketEntity;
import com.SocialWeb.entity.TicketCommentEntity;
import java.util.List;
import java.util.Map;

public interface SupportTicketService {

    void addTicketCommentByToken(String token, Long ticketId, String text);

    List<SupportTicketResponse> getAllSupportTicketResponses();

    void createSupportTicketByToken(String token, Map<String, Object> requestBody);

    List<SupportTicketResponse> getAllTicketsByToken(String token);

    void addTicketComment(Long ticket_id, String username, String text);

    SupportTicketEntity findSupportTicket(Long ticket_id);

    void updateSupportTicket(String token, String content, Long ticketId);

    void updateTicketComment(Long comment_id, String new_content);

    void deleteTicketComment(Long comment_id);

    void deleteSupportTicket(Long ticketId);

}
