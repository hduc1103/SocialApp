package com.SocialWeb.domain.response;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;


@Builder
@Data
public class PostResponse {
    private Long id;
    private String content;
    private List<CommentResponse> comments;
    private Date createdAt;

    private Date updatedAt;
    private Long user_id;

    public PostResponse(Long id, String content, List<CommentResponse> comments, Date createdAt, Date updatedAt, Long userId) {
        this.id = id;
        this.content = content;
        this.comments = comments;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.user_id = user_id;
    }
}
