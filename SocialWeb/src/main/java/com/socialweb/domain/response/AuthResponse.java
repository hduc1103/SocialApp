package com.socialweb.domain.response;


import lombok.Getter;

@Getter
public class AuthResponse {
    private final String jwt;

    public AuthResponse(String jwt) {
        this.jwt = jwt;
    }
}
