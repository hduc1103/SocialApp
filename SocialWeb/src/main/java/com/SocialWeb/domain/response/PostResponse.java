package com.SocialWeb.domain.response;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class PostResponse {
    private Long id;
    private String content;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Integer likeCount;

}
