package com.SocialWeb.domain.response;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class CommentResponse {
    private Long id;
    private Long user_id;
    private String text;
    private Date createdAt;
    private Date updatedAt;
    private boolean isDeleted;
}
