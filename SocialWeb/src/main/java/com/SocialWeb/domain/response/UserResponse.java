package com.SocialWeb.domain.response;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String img_url;
    private String bio;
    private String address;

    public UserResponse(Long id, String username, String email, String img_url, String bio, String address) {
        this.id = id;
        this.address = address;
        this.email = email;
        this.bio = bio;
        this.img_url = img_url;
        this.username = username;
    }
}
