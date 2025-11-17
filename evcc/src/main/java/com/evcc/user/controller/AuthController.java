package com.evcc.user.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.evcc.user.dto.AuthResponse;
import com.evcc.user.dto.LoginRequest;
import com.evcc.user.dto.RegisterRequest;
import com.evcc.user.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthService authService, PasswordEncoder passwordEncoder) {
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Đăng ký user mới
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        String username = request != null ? request.getUsername() : "null";
        logger.info("Register request received for username: {}", username);
        
        if (request == null) {
            AuthResponse response = AuthResponse.failure("Request không được để trống");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        try {
            AuthResponse response = authService.register(request);
            
            if (response.isSuccess()) {
                logger.info("Registration successful for user: {}", request.getUsername());
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                logger.warn("Registration failed for user: {} - {}", request.getUsername(), response.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            logger.error("Unexpected error during registration for user: {}", request.getUsername(), e);
            AuthResponse errorResponse = AuthResponse.failure("Có lỗi xảy ra trong hệ thống");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Đăng nhập
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        String username = request != null ? request.getUsername() : "null";
        logger.info("Login request received for username: {}", username);
        
        if (request == null) {
            AuthResponse response = AuthResponse.failure("Request không được để trống");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        try {
            AuthResponse response = authService.login(request);
            
            if (response.isSuccess()) {
                logger.info("Login successful for user: {}", request.getUsername());
                return ResponseEntity.ok(response);
            } else {
                logger.warn("Login failed for user: {} - {}", request.getUsername(), response.getMessage());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            logger.error("Unexpected error during login for user: {}", request.getUsername(), e);
            AuthResponse errorResponse = AuthResponse.failure("Có lỗi xảy ra trong hệ thống");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Kiểm tra xem username đã tồn tại chưa
     * GET /api/auth/check-username?username=abc
     */
    @GetMapping("/check-username")
    public ResponseEntity<AuthResponse> checkUsername(@RequestParam String username) {
        logger.info("Username check request for: {}", username);
        
        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(AuthResponse.failure("Username không được để trống"));
        }
        
        boolean exists = authService.userExists(username);
        
        if (exists) {
            return ResponseEntity.ok(AuthResponse.failure("Username đã tồn tại"));
        } else {
            return ResponseEntity.ok(AuthResponse.success("Username có thể sử dụng", null, null, null));
        }
    }

    /**
     * Health check endpoint
     * GET /api/auth/health
     */
    @GetMapping("/health")
    public ResponseEntity<AuthResponse> health() {
        return ResponseEntity.ok(AuthResponse.success("Auth service is running", null, null, null));
    }
}