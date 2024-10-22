package com.socialweb.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketCommentResponse {
    private Long id;
    private String text;
    private Date createdAt;
    private String name;
}