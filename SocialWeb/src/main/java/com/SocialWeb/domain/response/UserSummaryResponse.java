package com.SocialWeb.domain.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserSummaryResponse {
    private Long id;
    private String username;
    private String email;

    public UserSummaryResponse(Long id,String username, String email ) {
        this.id = id;
        this.username = username;
        this.email = email;
    }
}
