package com.socialweb.service.interfaces;

import com.socialweb.domain.response.SupportTicketResponse;
import com.socialweb.entity.SupportTicketEntity;

import java.util.List;
import java.util.Map;

public interface SupportTicketService {

    void addTicketCommentByToken(String token, Long ticketId, String text);

    List<SupportTicketResponse> getAllSupportTicketResponses();

    void createSupportTicketByToken(String token, Map<String, Object> requestBody);

    List<SupportTicketResponse> getAllTicketsByToken(String token);

    void addTicketComment(Long ticket_id, String username, String text);

    SupportTicketEntity findSupportTicket(Long ticket_id);

    void deleteSupportTicket(Long ticketId);

}
