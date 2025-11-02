package com.evcc.user.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evcc.user.dto.UpdateUserProfileRequest;
import com.evcc.user.dto.UserProfileResponse;
import com.evcc.user.dto.UserStatsResponse;
import com.evcc.user.entity.Role;
import com.evcc.user.entity.User;
import com.evcc.user.repository.UserRepository;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Lấy thông tin profile của user
     */
    public UserProfileResponse getUserProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy user với ID: " + userId));

        Set<String> roleNames = user.getRoles() != null ? 
                user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()) : 
                Set.of();

        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .citizenId(user.getCitizenId())
                .driverLicense(user.getDriverLicense())
                .isVerified(user.getIsVerified())
                .roles(roleNames)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    /**
     * Cập nhật thông tin profile của user
     * User không thể tự thay đổi trạng thái xác minh (isVerified)
     */
    public UserProfileResponse updateUserProfile(UUID userId, UpdateUserProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy user với ID: " + userId));

        // Cập nhật các trường nếu có giá trị mới
        if (request.getCitizenId() != null) {
            user.setCitizenId(request.getCitizenId());
        }

        if (request.getDriverLicense() != null) {
            user.setDriverLicense(request.getDriverLicense());
        }

        // Lưu ý: isVerified không được cập nhật ở đây - chỉ admin mới có thể thay đổi

        User savedUser = userRepository.save(user);
        return getUserProfile(savedUser.getId());
    }

    /**
     * Xác minh tài khoản user (chỉ admin mới được gọi)
     */
    public UserProfileResponse verifyUser(UUID userId, UUID adminId) {
        // Kiểm tra quyền admin
        checkAdminPermission(adminId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy user với ID: " + userId));

        // Kiểm tra user đã có đầy đủ thông tin CCCD và bằng lái xe chưa
        if (user.getCitizenId() == null || user.getCitizenId().trim().isEmpty()) {
            throw new IllegalArgumentException("User chưa cập nhật số căn cước công dân");
        }
        
        if (user.getDriverLicense() == null || user.getDriverLicense().trim().isEmpty()) {
            throw new IllegalArgumentException("User chưa cập nhật số bằng lái xe");
        }

        user.setIsVerified(true);
        User savedUser = userRepository.save(user);
        return getUserProfile(savedUser.getId());
    }

    /**
     * Lấy danh sách user chưa xác minh (chỉ admin)
     */
    public List<UserProfileResponse> getUnverifiedUsers(UUID adminId) {
        // Kiểm tra quyền admin
        checkAdminPermission(adminId);
        
        List<User> unverifiedUsers = userRepository.findByIsVerifiedFalse();
        return unverifiedUsers.stream()
                .map(user -> {
                    Set<String> roleNames = user.getRoles() != null ? 
                            user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()) : 
                            Set.of();
                    
                    return UserProfileResponse.builder()
                            .id(user.getId())
                            .username(user.getUsername())
                            .citizenId(user.getCitizenId())
                            .driverLicense(user.getDriverLicense())
                            .isVerified(user.getIsVerified())
                            .roles(roleNames)
                            .createdAt(user.getCreatedAt())
                            .updatedAt(user.getUpdatedAt())
                            .build();
                })
                .toList();
    }

    /**
     * Lấy thông tin profile của user khác (chỉ admin)
     */
    public UserProfileResponse getUserProfileByAdmin(UUID userId, UUID adminId) {
        // Kiểm tra quyền admin
        checkAdminPermission(adminId);
        
        return getUserProfile(userId);
    }

    /**
     * Kiểm tra quyền admin
     */
    private void checkAdminPermission(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new SecurityException("Không tìm thấy user"));
        
        boolean isAdmin = user.getRoles() != null && 
                user.getRoles().stream()
                    .anyMatch(role -> "ADMIN".equals(role.getName()));
        
        if (!isAdmin) {
            throw new SecurityException("Chỉ có admin mới được thực hiện hành động này");
        }
    }

    /**
     * Tìm user theo username
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Kiểm tra user có tồn tại theo ID không
     */
    public boolean existsById(UUID userId) {
        return userRepository.existsById(userId);
    }

    /**
     * Lấy thống kê user (chỉ admin)
     */
    public UserStatsResponse getUserStats(UUID adminId) {
        // Kiểm tra quyền admin
        checkAdminPermission(adminId);
        
        long totalUsers = userRepository.count();
        long verifiedUsers = userRepository.countByIsVerifiedTrue();
        long unverifiedUsers = userRepository.countByIsVerifiedFalse();
        
        List<User> allUsers = userRepository.findAll();
        long usersWithCompleteInfo = allUsers.stream()
                .mapToLong(user -> {
                    boolean hasCompleteInfo = user.getCitizenId() != null && 
                            !user.getCitizenId().trim().isEmpty() &&
                            user.getDriverLicense() != null && 
                            !user.getDriverLicense().trim().isEmpty();
                    return hasCompleteInfo ? 1 : 0;
                })
                .sum();
        
        long usersWithIncompleteInfo = totalUsers - usersWithCompleteInfo;
        
        return UserStatsResponse.builder()
                .totalUsers(totalUsers)
                .verifiedUsers(verifiedUsers)
                .unverifiedUsers(unverifiedUsers)
                .usersWithCompleteInfo(usersWithCompleteInfo)
                .usersWithIncompleteInfo(usersWithIncompleteInfo)
                .build();
    }
}
