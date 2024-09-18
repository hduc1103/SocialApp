package com.SocialWeb.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtService {

    private static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";
    private static final long EXPIRATION_TIME = 1000 * 60 * 30; // 30 minutes

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateToken(String userName) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userName);
    }

    private String createToken(Map<String, Object> claims, String userName) {
        long now = System.currentTimeMillis();
        long expirationTime = now + EXPIRATION_TIME;

        // Create the header
        Map<String, Object> header = new HashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");

        // Encode header and payload
        String encodedHeader;
        String encodedPayload;
        try {
            encodedHeader = Base64.getUrlEncoder().encodeToString(objectMapper.writeValueAsBytes(header));
            encodedPayload = Base64.getUrlEncoder().encodeToString(objectMapper.writeValueAsBytes(new JwtPayload(userName, expirationTime)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize header or payload", e);
        }

        // Sign the token
        String signature = sign(encodedHeader + "." + encodedPayload, SECRET);

        return encodedHeader + "." + encodedPayload + "." + signature;
    }

    private String sign(String data, String secret) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmac.init(keySpec);
            byte[] signatureBytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().encodeToString(signatureBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to sign JWT", e);
        }
    }

    public String extractUsername(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new RuntimeException("Invalid token");
        }

        String payload;
        try {
            payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Failed to decode token payload", e);
        }

        JwtPayload payloadObj;
        try {
            payloadObj = objectMapper.readValue(payload, JwtPayload.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize token payload", e);
        }
        return payloadObj.getSub();
    }

    public Date extractExpiration(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new RuntimeException("Invalid token");
        }

        String payload;
        try {
            payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Failed to decode token payload", e);
        }

        JwtPayload payloadObj;
        try {
            payloadObj = objectMapper.readValue(payload, JwtPayload.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize token payload", e);
        }
        return new Date(payloadObj.getExp());
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        Date expiration = extractExpiration(token);
        return expiration.before(new Date());
    }

    private static class JwtPayload {
        private String sub;
        private long exp;

        public JwtPayload() { }

        public JwtPayload(String sub, long exp) {
            this.sub = sub;
            this.exp = exp;
        }

        public String getSub() {
            return sub;
        }

        public void setSub(String sub) {
            this.sub = sub;
        }

        public long getExp() {
            return exp;
        }

        public void setExp(long exp) {
            this.exp = exp;
        }
    }
}
