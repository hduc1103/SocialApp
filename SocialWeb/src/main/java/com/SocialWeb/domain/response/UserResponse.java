package com.SocialWeb.domain.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserResponse {
    private Long id;
    private String username;
    private String name;
    private String email;
    private String img_url;
    private String bio;
    private String address;

    public UserResponse(Long id, String username, String name, String email, String img_url, String bio, String address) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.email = email;
        this.img_url = img_url;
        this.bio = bio;
        this.address = address;
    }

    public UserResponse(Long id, String name, String username, String email) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
    }
}
