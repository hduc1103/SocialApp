package com.socialweb.service.impl;

import com.socialweb.domain.response.SupportTicketResponse;
import com.socialweb.domain.response.TicketCommentResponse;
import com.socialweb.entity.SupportTicketEntity;
import com.socialweb.entity.TicketCommentEntity;
import com.socialweb.config.TicketStatus;
import com.socialweb.entity.UserEntity;
import com.socialweb.repository.SupportTicketRepository;
import com.socialweb.repository.TicketCommentRepository;
import com.socialweb.repository.UserRepository;
import com.socialweb.security.JwtUtil;
import com.socialweb.service.interfaces.SupportTicketService;
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
                .build();

        ticketCommentRepository.save(ticketCommentEntity);
    }

    @Override
    public void deleteSupportTicket(Long ticketId) {
        SupportTicketEntity supportTicketEntity = supportTicketRepository.findById(ticketId).orElseThrow();
        supportTicketEntity.setStatus(TicketStatus.CLOSED);
        supportTicketEntity.setEndAt(new Date());
        supportTicketRepository.save(supportTicketEntity);
    }

    @Override
    public List<SupportTicketResponse> getAllSupportTicketResponses() {
        List<SupportTicketEntity> supportTickets = supportTicketRepository.findAll();
        return supportTickets.stream()
                .map(ticket -> SupportTicketResponse.builder()
                        .id(ticket.getId())
                        .title(ticket.getTitle())
                        .content(ticket.getContent())
                        .status(ticket.getStatus().toString())
                        .author(ticket.getUser().getName())
                        .createdAt(ticket.getCreatedAt())
                        .endAt(ticket.getEndAt())
                        .userId(ticket.getUser().getId())
                        .comments(ticket.getTicketCommentEntities().stream()
                                .map(comment -> TicketCommentResponse.builder()
                                        .id(comment.getId())
                                        .text(comment.getText())
                                        .createdAt(comment.getCreatedAt())
                                        .author(comment.getUser().getName())
                                        .name(comment.getUser().getName())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void createSupportTicketByToken(String token, Map<String, Object> requestBody) {
        String username = extractUsername(token);
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + username));
        String title = (String) requestBody.get("title");
        String content = (String) requestBody.get("content");
        SupportTicketEntity supportTicketEntity = SupportTicketEntity.builder()
                .user(userEntity)
                .title(title)
                .content(content)
                .status(TicketStatus.IN_PROGRESS)
                .createdAt(new Date())
                .build();
        supportTicketRepository.save(supportTicketEntity);
    }

    @Override
    public List<SupportTicketResponse> getAllTicketsByToken(String token) {
        String username = extractUsername(token);
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<SupportTicketEntity> supportTickets = supportTicketRepository.findByUserId(userEntity.getId());
        return supportTickets.stream()
                .map(ticket -> SupportTicketResponse.builder()
                        .id(ticket.getId())
                        .title(ticket.getTitle())
                        .content(ticket.getContent())
                        .status(ticket.getStatus().toString())
                        .createdAt(ticket.getCreatedAt())
                        .userId(ticket.getUser().getId())
                        .comments(ticket.getTicketCommentEntities().stream()
                                .map(comment -> TicketCommentResponse.builder()
                                        .id(comment.getId())
                                        .text(comment.getText())
                                        .createdAt(comment.getCreatedAt())
                                        .name(comment.getUser().getName())
                                        .build())
                                .toList())
                        .build())
                .toList();
    }

    @Override
    public void addTicketComment(Long ticket_id, String token, String text) {
        String username = extractUsername(token);
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        SupportTicketEntity supportTicketEntity = findSupportTicket(ticket_id);
        TicketCommentEntity ticketCommentEntity = TicketCommentEntity.builder()
                .text(text)
                .user(userEntity)
                .supportTicketEntity(supportTicketEntity)
                .createdAt(new Date())
                .build();
        ticketCommentRepository.save(ticketCommentEntity);
    }

    @Override
    public SupportTicketEntity findSupportTicket(Long ticket_id) {
        return supportTicketRepository.findById(ticket_id).orElseThrow(() -> new NoSuchElementException("Ticket not found"));
    }
}
