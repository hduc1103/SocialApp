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
    private String type;
    private Long relatedId;
    private Long senderId;
    private String imgUrl;

    @Builder
    public NotificationResponse(Long id, Long relatedId, String type, Long userId, Date createdAt, String content, Long senderId, String imgUrl) {
        this.imgUrl = imgUrl;
        this.id = id;
        this.relatedId = relatedId;
        this.type = type;
        this.userId = userId;
        this.createdAt = createdAt;
        this.content = content;
        this.senderId = senderId;
    }
}
