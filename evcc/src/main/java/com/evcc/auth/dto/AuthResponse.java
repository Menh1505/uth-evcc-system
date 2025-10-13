package com.evcc.auth.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class AuthResponse {
    
    private String token;
    private String tokenType = "Bearer";
    private UUID userId;
    private String username;
    private String status;
    private LocalDateTime expiresAt;
    
    // Constructors
    public AuthResponse() {}
    
    public AuthResponse(String token, UUID userId, String username, String status, LocalDateTime expiresAt) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.status = status;
        this.expiresAt = expiresAt;
    }
    
    // Getters and Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getTokenType() {
        return tokenType;
    }
    
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
    
    public UUID getUserId() {
        return userId;
    }
    
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}
