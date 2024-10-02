package com.SocialWeb.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportTicketResponse {
    private Long id;
    private String title;
    private String content;
    private Date createdAt;
    private Date endAt;
    private Long userId;
    private String status;
    private List<TicketCommentResponse> comments;
}
