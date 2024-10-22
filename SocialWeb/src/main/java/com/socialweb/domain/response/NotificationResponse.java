package com.socialweb.domain.response;

import lombok.Builder;
import lombok.Data;

import java.util.Date;


@Data
public class NotificationResponse {
    private Long id;
    private String content;
    private Date createdAt;
    private Long userId;

    @Builder
    public NotificationResponse(Long id, Long userId, Date createdAt, String content) {
        this.id = id;
        this.userId = userId;
        this.createdAt = createdAt;
        this.content = content;
    }
}
