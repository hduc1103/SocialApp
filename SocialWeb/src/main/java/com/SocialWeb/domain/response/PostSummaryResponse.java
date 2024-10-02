package com.SocialWeb.domain.response;

import lombok.Builder;
import lombok.Data;


@Builder
@Data
public class PostSummaryResponse {
    private Long id;
    private String content;

    public PostSummaryResponse(Long id, String content) {
        this.id = id;
        this.content = content;
    }
}

