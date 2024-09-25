package com.SocialWeb.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class PostDTO {
    private Long id;
    private String content;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Integer likeCount;

}
