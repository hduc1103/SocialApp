package com.socialweb.domain.request;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
}
