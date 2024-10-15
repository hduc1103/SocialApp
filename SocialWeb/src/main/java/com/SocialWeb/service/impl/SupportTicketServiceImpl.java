package com.SocialWeb.service.impl;

import com.SocialWeb.domain.response.SupportTicketResponse;
import com.SocialWeb.domain.response.TicketCommentResponse;
import com.SocialWeb.entity.*;
import com.SocialWeb.repository.SupportTicketRepository;
import com.SocialWeb.repository.TicketCommentRepository;
import com.SocialWeb.repository.UserRepository;
import com.SocialWeb.security.JwtUtil;
import com.SocialWeb.service.interfaces.SupportTicketService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class SupportTicketServiceImpl implements SupportTicketService {

    private final SupportTicketRepository supportTicketRepository;
    private final TicketCommentRepository ticketCommentRepository;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private String extractUsername(String token) {
        String jwtToken = token.substring(7);
        return jwtUtil.extractUsername(jwtToken);
    }

    public SupportTicketServiceImpl(SupportTicketRepository supportTicketRepository, TicketCommentRepository ticketCommentRepository, JwtUtil jwtUtil, UserRepository userRepository) {
        this.supportTicketRepository = supportTicketRepository;
        this.ticketCommentRepository = ticketCommentRepository;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }
    @Override
    public void addTicketCommentByToken(String token, Long ticketId, String text) {
        String username = extractUsername(token);
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + username));

        SupportTicketEntity supportTicketEntity = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new NoSuchElementException("Support ticket not found: " + ticketId));

        TicketCommentEntity ticketCommentEntity = TicketCommentEntity.builder()
                .text(text)
                .user(userEntity)
                .supportTicketEntity(supportTicketEntity)
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();

        ticketCommentRepository.save(ticketCommentEntity);
    }


    @Override
    public void updateSupportTicket(String token, String content, Long ticketId) {
        String username = extractUsername(token);  // Extract username from token
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        Long userId = userEntity.getId();

        if (!checkUserAccessToTicket(userId, ticketId)) {
            throw new AccessDeniedException("Access denied to the ticket");
        }

        SupportTicketEntity supportTicketEntity = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new NoSuchElementException("Ticket not found"));

        updateTicketContent(supportTicketEntity, content);
    }

    private boolean checkUserAccessToTicket(Long userId, Long ticketId) {
        return supportTicketRepository.checkUserTicket(userId) == ticketId;
    }

    private void updateTicketContent(SupportTicketEntity ticketEntity, String content) {
        ticketEntity.setContent(content);
        supportTicketRepository.save(ticketEntity);
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
    public void deleteSupportTicket(Long ticketId) {
        supportTicketRepository.delete(supportTicketRepository.findById(ticketId).orElseThrow());
    }

    @Override
    public List<SupportTicketResponse> getAllSupportTicketResponses() {
        List<SupportTicketEntity> supportTickets = supportTicketRepository.findAll();
        return supportTickets.stream()
                .map(ticket -> SupportTicketResponse.builder()
                        .id(ticket.getId())
                        .title(ticket.getTitle())
                        .content(ticket.getContent())
                        .status(ticket.getStatus())
                        .createdAt(ticket.getCreatedAt())
                        .endAt(ticket.getEndAt())
                        .userId(ticket.getUser().getId())
                        .comments(ticket.getTicketCommentEntities().stream()
                                .map(comment -> TicketCommentResponse.builder()
                                        .id(comment.getId())
                                        .text(comment.getText())
                                        .createdAt(comment.getCreatedAt())
                                        .updatedAt(comment.getUpdatedAt())
                                        .userId(comment.getUser().getId())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }
    @Override
    public void createSupportTicketByToken(String token, Map<String, Object> requestBody) {
        String username = extractUsername(token);

        // Find the user by username
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + username));

        // Extract the title and content from request body
        String title = (String) requestBody.get("title");
        String content = (String) requestBody.get("content");

        // Build the support ticket entity
        SupportTicketEntity supportTicketEntity = SupportTicketEntity.builder()
                .user(userEntity)
                .title(title)
                .content(content)
                .status("In progress")
                .createdAt(new Date())
                .build();

        // Save the ticket
        supportTicketRepository.save(supportTicketEntity);
    }

    @Override
    public List<SupportTicketResponse> getAllTicketsByToken(String token) {
        String username = extractUsername(token);

        // Fetch the user entity by username
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Get all support tickets for the user
        List<SupportTicketEntity> supportTickets = supportTicketRepository.findByUserId(userEntity.getId());

        // Convert the list of tickets to SupportTicketResponse objects
        return supportTickets.stream()
                .map(ticket -> SupportTicketResponse.builder()
                        .id(ticket.getId())
                        .title(ticket.getTitle())
                        .content(ticket.getContent())
                        .status(ticket.getStatus())
                        .createdAt(ticket.getCreatedAt())
                        .userId(ticket.getUser().getId())
                        .comments(ticket.getTicketCommentEntities().stream()
                                .map(comment -> TicketCommentResponse.builder()
                                        .id(comment.getId())
                                        .text(comment.getText())
                                        .createdAt(comment.getCreatedAt())
                                        .updatedAt(comment.getUpdatedAt())
                                        .userId(comment.getUser().getId())
                                        .build())
                                .toList())
                        .build())
                .toList();
    }

    @Override
    public void addTicketComment(Long ticket_id, String username, String text) {
        // Find the user by username
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        // Find the support ticket by id
        SupportTicketEntity supportTicketEntity = findSupportTicket(ticket_id);

        // Create a new ticket comment
        TicketCommentEntity ticketCommentEntity = TicketCommentEntity.builder()
                .text(text)
                .user(userEntity)
                .supportTicketEntity(supportTicketEntity)
                .createdAt(new Date())
                .build();

        // Save the comment
        ticketCommentRepository.save(ticketCommentEntity);
    }

    @Override
    public SupportTicketEntity findSupportTicket(Long ticket_id) {
        return supportTicketRepository.findById(ticket_id).orElseThrow(() -> new NoSuchElementException("Ticket not found"));
    }
}
