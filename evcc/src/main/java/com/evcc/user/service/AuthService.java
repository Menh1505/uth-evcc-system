package com.evcc.user.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evcc.security.JwtUtils;
import com.evcc.user.dto.AuthResponse;
import com.evcc.user.dto.LoginRequest;
import com.evcc.user.dto.RegisterRequest;
import com.evcc.user.entity.Role;
import com.evcc.user.entity.User;
import com.evcc.user.repository.RoleRepository;
import com.evcc.user.repository.UserRepository;

@Service
public class AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private static final String DEFAULT_ROLE = "USER";
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository, 
                      AuthenticationManager authenticationManager, JwtUtils jwtUtils,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Đăng ký user mới với role USER mặc định
     */
    @Transactional(rollbackFor = Exception.class)
    public AuthResponse register(RegisterRequest request) {
        logger.info("Attempting to register new user: {}", request.getUsername());
        
        // Validate input - không rollback cho validation errors
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            logger.warn("Registration failed: Username is empty");
            return AuthResponse.failure("Username không được để trống");
        }
        
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            logger.warn("Registration failed: Password is empty for user: {}", request.getUsername());
            return AuthResponse.failure("Password không được để trống");
        }

        // Check if username already exists - không rollback cho business logic errors
        if (userRepository.existsByUsername(request.getUsername())) {
            logger.warn("Registration failed: Username already exists: {}", request.getUsername());
            return AuthResponse.failure("Username đã tồn tại");
        }

        try {
            // Get or create USER role first
            Role userRole = getOrCreateUserRole();
            
            // Create new user with encoded password
            User newUser = new User(request.getUsername(), passwordEncoder.encode(request.getPassword()));
            
            // Set default role USER
            Set<Role> roles = new HashSet<>();
            roles.add(userRole);
            newUser.setRoles(roles);
            
            // Save user
            User savedUser = userRepository.save(newUser);
            
            // Convert roles to string set for response
            Set<String> roleNames = savedUser.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet());
            
            logger.info("User registered successfully: {} with ID: {}", savedUser.getUsername(), savedUser.getId());
            
            return AuthResponse.success(
                "Đăng ký thành công", 
                savedUser.getId(), 
                savedUser.getUsername(), 
                roleNames
            );
            
        } catch (Exception e) {
            logger.error("Registration transaction failed for user: {}", request.getUsername(), e);
            return AuthResponse.failure("Đăng ký thất bại: Có lỗi xảy ra trong quá trình tạo tài khoản");
        }
    }

    /**
     * Đăng nhập với username và password
     */
    public AuthResponse login(LoginRequest request) {
        logger.info("Attempting login for user: {}", request.getUsername());
        
        // Validate input
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            logger.warn("Login failed: Username is empty");
            return AuthResponse.failure("Username không được để trống");
        }
        
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            logger.warn("Login failed: Password is empty for user: {}", request.getUsername());
            return AuthResponse.failure("Password không được để trống");
        }

        try {
            // Authenticate user với Spring Security
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            
            // Generate JWT token
            String jwt = jwtUtils.generateJwtToken(authentication);
            
            // Get user details
            User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalStateException("User not found after successful authentication"));
            
            // Convert roles to string set for response
            Set<String> roleNames = user.getRoles() != null ? 
                    user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()) : 
                    new HashSet<>();
            
            logger.info("Login successful for user: {} with ID: {}", user.getUsername(), user.getId());
            
            return AuthResponse.success(
                "Đăng nhập thành công", 
                user.getId(), 
                user.getUsername(), 
                roleNames,
                jwt
            );
            
        } catch (org.springframework.security.core.AuthenticationException e) {
            logger.warn("Authentication failed for user: {} - {}", request.getUsername(), e.getMessage());
            return AuthResponse.failure("Username hoặc password không đúng");
        } catch (IllegalStateException e) {
            logger.error("User state error during login for user: {}", request.getUsername(), e);
            return AuthResponse.failure("Có lỗi xảy ra trong quá trình đăng nhập");
        } catch (Exception e) {
            logger.error("Unexpected error during login for user: {}", request.getUsername(), e);
            return AuthResponse.failure("Có lỗi xảy ra trong quá trình đăng nhập");
        }
    }

    /**
     * Get or create USER role
     * This method is called within a transaction context
     */
    private Role getOrCreateUserRole() {
        // Try to find existing role first
        Optional<Role> existingRole = roleRepository.findByName(DEFAULT_ROLE);
        
        if (existingRole.isPresent()) {
            logger.debug("Found existing USER role with ID: {}", existingRole.get().getId());
            return existingRole.get();
        }
        
        // Create new USER role if not exists
        logger.info("Creating default USER role");
        try {
            Role userRole = Role.builder()
                    .name(DEFAULT_ROLE)
                    .build();
            
            Role savedRole = roleRepository.save(userRole);
            logger.info("Created default USER role with ID: {}", savedRole.getId());
            return savedRole;
            
        } catch (Exception e) {
            logger.error("Failed to create default USER role", e);
            // Try to find again in case another thread created it
            return roleRepository.findByName(DEFAULT_ROLE)
                    .orElseThrow(() -> new IllegalStateException("Unable to create or find USER role"));
        }
    }

    /**
     * Check if user exists by username
     */
    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Get user by username
     */
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}