package com.SocialWeb.dto;

import lombok.Data;

@Data
public class PostResponse {
    private Long id;
    private String content;
    private Long numberoflike;

    public PostResponse(Long id, String content, Long numberoflike) {
        this.id = id;
        this.content = content;
        this.numberoflike = numberoflike;
    }

}
