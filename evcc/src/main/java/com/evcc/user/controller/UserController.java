package com.evcc.user.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.evcc.user.dto.UpdateUserProfileRequest;
import com.evcc.user.dto.UserProfileResponse;
import com.evcc.user.dto.UserStatsResponse;
import com.evcc.user.entity.User;
import com.evcc.user.repository.UserRepository;
import com.evcc.user.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    /**
     * Lấy danh sách tất cả users (có thể cần authorization admin)
     */
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * Tạo user mới (có thể không cần thiết vì đã có /api/auth/register)
     */
    @PostMapping
    public User saveUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    /**
     * Lấy thông tin profile của user hiện tại
     * GET /api/users/profile
     */
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getMyProfile() {
        UUID userId = getCurrentUserId();
        UserProfileResponse profile = userService.getUserProfile(userId);
        return ResponseEntity.ok(profile);
    }

    /**
     * Cập nhật thông tin profile của user hiện tại
     * PUT /api/users/profile
     */
    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateMyProfile(
            @Valid @RequestBody UpdateUserProfileRequest request) {
        
        UUID userId = getCurrentUserId();
        UserProfileResponse updatedProfile = userService.updateUserProfile(userId, request);
        return ResponseEntity.ok(updatedProfile);
    }

    /**
     * Xác minh user (chỉ admin mới được gọi)
     * PUT /api/users/{userId}/verify
     */
    @PutMapping("/{userId}/verify")
    public ResponseEntity<UserProfileResponse> verifyUser(@PathVariable UUID userId) {
        UUID adminId = getCurrentUserId();
        UserProfileResponse verifiedUser = userService.verifyUser(userId, adminId);
        return ResponseEntity.ok(verifiedUser);
    }

    /**
     * Lấy danh sách user chưa xác minh (chỉ admin)
     * GET /api/users/unverified
     */
    @GetMapping("/unverified")
    public ResponseEntity<List<UserProfileResponse>> getUnverifiedUsers() {
        UUID adminId = getCurrentUserId();
        List<UserProfileResponse> unverifiedUsers = userService.getUnverifiedUsers(adminId);
        return ResponseEntity.ok(unverifiedUsers);
    }

    /**
     * Lấy thông tin profile của user khác (chỉ admin)
     * GET /api/users/{userId}/profile
     */
    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable UUID userId) {
        UUID adminId = getCurrentUserId();
        UserProfileResponse profile = userService.getUserProfileByAdmin(userId, adminId);
        return ResponseEntity.ok(profile);
    }

    /**
     * Lấy thống kê user (chỉ admin)
     * GET /api/users/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<UserStatsResponse> getUserStats() {
        UUID adminId = getCurrentUserId();
        UserStatsResponse stats = userService.getUserStats(adminId);
        return ResponseEntity.ok(stats);
    }

    /**
     * Lấy User ID từ Spring Security Authentication
     */
    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("User chưa đăng nhập");
        }
        
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new SecurityException("Không tìm thấy user: " + username));
        
        return user.getId();
    }
}
