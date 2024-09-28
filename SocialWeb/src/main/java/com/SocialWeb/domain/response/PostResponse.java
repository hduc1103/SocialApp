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
}