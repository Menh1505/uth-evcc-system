package com.evcc.auth.service;

import com.evcc.auth.config.JwtUtil;
import com.evcc.auth.dto.AuthResponse;
import com.evcc.auth.dto.LoginRequest;
import com.evcc.auth.dto.RegisterRequest;
import com.evcc.user.entity.User;
import com.evcc.user.entity.UserStatus;
import com.evcc.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Authenticate user and generate JWT token
     */
    public AuthResponse login(LoginRequest loginRequest) {
        Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsername());
        
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with username: " + loginRequest.getUsername());
        }
        
        User user = userOpt.get();
        
        // Check if user account is active
        if (!UserStatus.ACTIVE.equals(user.getStatus())) {
            throw new RuntimeException("User account is not active. Status: " + user.getStatus());
        }
        
        // Validate password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }
        
        // Generate JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        LocalDateTime expiresAt = jwtUtil.getExpirationDateFromToken(token);
        
        return new AuthResponse(
            token, 
            user.getId(), 
            user.getUsername(), 
            user.getStatus().toString(),
            expiresAt
        );
    }

    /**
     * Register new user
     */
    public AuthResponse register(RegisterRequest registerRequest) {
        // Validate password confirmation
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new RuntimeException("Password confirmation does not match");
        }
        
        // Check if username already exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Username already exists: " + registerRequest.getUsername());
        }
        
        // Check if phone already exists (if provided)
        if (registerRequest.getPhone() != null && 
            !registerRequest.getPhone().trim().isEmpty() && 
            userRepository.existsByPhone(registerRequest.getPhone())) {
            throw new RuntimeException("Phone number already exists: " + registerRequest.getPhone());
        }
        
        // Hash password
        String hashedPassword = passwordEncoder.encode(registerRequest.getPassword());
        
        // Create new user
        User newUser = new User(
            registerRequest.getUsername(),
            registerRequest.getPhone(),
            hashedPassword,
            UserStatus.ACTIVE
        );
        
        User savedUser = userRepository.save(newUser);
        
        // Generate JWT token for the new user
        String token = jwtUtil.generateToken(savedUser.getId(), savedUser.getUsername());
        LocalDateTime expiresAt = jwtUtil.getExpirationDateFromToken(token);
        
        return new AuthResponse(
            token,
            savedUser.getId(),
            savedUser.getUsername(),
            savedUser.getStatus().toString(),
            expiresAt
        );
    }

    /**
     * Validate JWT token and get user info
     */
    public User validateTokenAndGetUser(String token) {
        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("Invalid JWT token");
        }
        
        if (jwtUtil.isTokenExpired(token)) {
            throw new RuntimeException("JWT token has expired");
        }
        
        String username = jwtUtil.getUsernameFromToken(token);
        Optional<User> userOpt = userRepository.findByUsername(username);
        
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found for token");
        }
        
        User user = userOpt.get();
        
        // Check if user is still active
        if (!UserStatus.ACTIVE.equals(user.getStatus())) {
            throw new RuntimeException("User account is no longer active");
        }
        
        return user;
    }

    /**
     * Refresh JWT token
     */
    public AuthResponse refreshToken(String token) {
        User user = validateTokenAndGetUser(token);
        
        String newToken = jwtUtil.generateToken(user.getId(), user.getUsername());
        LocalDateTime expiresAt = jwtUtil.getExpirationDateFromToken(newToken);
        
        return new AuthResponse(
            newToken,
            user.getId(),
            user.getUsername(),
            user.getStatus().toString(),
            expiresAt
        );
    }
}
