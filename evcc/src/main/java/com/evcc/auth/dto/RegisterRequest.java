package com.evcc.auth.dto;

import jakarta.validation.constraints.NotBlank;

public class RegisterRequest {
    
    @NotBlank(message = "Username is required")
    private String username;
    
    private String phone;
    
    @NotBlank(message = "Password is required")
    private String password;
    
    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
    
    // Constructors
    public RegisterRequest() {}
    
    public RegisterRequest(String username, String phone, String password, String confirmPassword) {
        this.username = username;
        this.phone = phone;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }
    
    // Getters and Setters
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getConfirmPassword() {
        return confirmPassword;
    }
    
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
