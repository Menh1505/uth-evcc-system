package com.evcc.auth.controller;

import com.evcc.auth.dto.AuthResponse;
import com.evcc.auth.dto.LoginRequest;
import com.evcc.auth.dto.RegisterRequest;
import com.evcc.auth.service.AuthService;
import com.evcc.user.entity.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * User login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse authResponse = authService.login(loginRequest);
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * User registration
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            AuthResponse authResponse = authService.register(registerRequest);
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Refresh JWT token
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid authorization header"));
            }
            
            String token = authHeader.substring(7);
            AuthResponse authResponse = authService.refreshToken(token);
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get current user profile (protected endpoint)
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid authorization header"));
            }
            
            String token = authHeader.substring(7);
            User user = authService.validateTokenAndGetUser(token);
            
            // Return user info without sensitive data
            Map<String, Object> userProfile = Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "phone", user.getPhone() != null ? user.getPhone() : "",
                "status", user.getStatus().toString(),
                "createdAt", user.getCreatedAt(),
                "updatedAt", user.getUpdatedAt()
            );
            
            return ResponseEntity.ok(userProfile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Validate token endpoint
     */
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid authorization header"));
            }
            
            String token = authHeader.substring(7);
            User user = authService.validateTokenAndGetUser(token);
            
            return ResponseEntity.ok(Map.of(
                "valid", true,
                "userId", user.getId(),
                "username", user.getUsername(),
                "status", user.getStatus().toString()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "valid", false,
                "error", e.getMessage()
            ));
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "Authentication Service",
            "timestamp", System.currentTimeMillis()
        ));
    }
}
