package com.SocialWeb.dto;

import lombok.Data;

@Data
public class PostResponse {
    private Long id;
    private String content;
    private Long userId;

    public PostResponse(Long id, String content, Long userId) {
        this.id = id;
        this.content = content;
        this.userId = userId;
    }

}
