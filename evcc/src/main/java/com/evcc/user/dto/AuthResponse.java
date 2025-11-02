package com.evcc.user.dto;

import java.util.Set;
import java.util.UUID;

public class AuthResponse {
    private boolean success;
    private String message;
    private UUID userId;
    private String username;
    private Set<String> roles;
    private String token;
    private String tokenType = "Bearer";

    // Constructors
    public AuthResponse() {}

    public AuthResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public AuthResponse(boolean success, String message, UUID userId, String username, Set<String> roles) {
        this.success = success;
        this.message = message;
        this.userId = userId;
        this.username = username;
        this.roles = roles;
    }

    public AuthResponse(boolean success, String message, UUID userId, String username, Set<String> roles, String token) {
        this.success = success;
        this.message = message;
        this.userId = userId;
        this.username = username;
        this.roles = roles;
        this.token = token;
    }

    // Static factory methods
    public static AuthResponse success(String message, UUID userId, String username, Set<String> roles) {
        return new AuthResponse(true, message, userId, username, roles);
    }

    public static AuthResponse success(String message, UUID userId, String username, Set<String> roles, String token) {
        return new AuthResponse(true, message, userId, username, roles, token);
    }

    public static AuthResponse failure(String message) {
        return new AuthResponse(false, message);
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

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

    @Override
    public String toString() {
        return "AuthResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", roles=" + roles +
                ", token='" + (token != null ? "[PROTECTED]" : null) + '\'' +
                ", tokenType='" + tokenType + '\'' +
                '}';
    }
}