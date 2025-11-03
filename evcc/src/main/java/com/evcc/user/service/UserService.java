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

    // >>> THÊM: tìm user theo ID (để ContractService dùng)
    public Optional<User> findById(UUID userId) {
        return userRepository.findById(userId);
    }

    /** Lấy thông tin profile của user */
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

    /** Cập nhật profile */
    public UserProfileResponse updateUserProfile(UUID userId, UpdateUserProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy user với ID: " + userId));

        if (request.getCitizenId() != null) user.setCitizenId(request.getCitizenId());
        if (request.getDriverLicense() != null) user.setDriverLicense(request.getDriverLicense());

        User savedUser = userRepository.save(user);
        return getUserProfile(savedUser.getId());
    }

    /** Xác minh tài khoản (admin) */
    public UserProfileResponse verifyUser(UUID userId, UUID adminId) {
        checkAdminPermission(adminId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy user với ID: " + userId));

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

    /** Lấy danh sách user chưa xác minh (admin) */
    public List<UserProfileResponse> getUnverifiedUsers(UUID adminId) {
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

    /** Lấy profile của user khác (admin) */
    public UserProfileResponse getUserProfileByAdmin(UUID userId, UUID adminId) {
        checkAdminPermission(adminId);
        return getUserProfile(userId);
    }

    /** Kiểm tra quyền admin */
    private void checkAdminPermission(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new SecurityException("Không tìm thấy user"));
        boolean isAdmin = user.getRoles() != null &&
                user.getRoles().stream().anyMatch(role -> "ADMIN".equals(role.getName()));
        if (!isAdmin) throw new SecurityException("Chỉ có admin mới được thực hiện hành động này");
    }

    /** Tìm user theo username (dùng cho principal -> entity) */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean existsById(UUID userId) {
        return userRepository.existsById(userId);
    }

    /** Thống kê (admin) */
    public UserStatsResponse getUserStats(UUID adminId) {
        checkAdminPermission(adminId);

        long totalUsers = userRepository.count();
        long verifiedUsers = userRepository.countByIsVerifiedTrue();
        long unverifiedUsers = userRepository.countByIsVerifiedFalse();
        List<User> allUsers = userRepository.findAll();

        long usersWithCompleteInfo = allUsers.stream().mapToLong(u -> {
            boolean ok = u.getCitizenId() != null && !u.getCitizenId().trim().isEmpty()
                    && u.getDriverLicense() != null && !u.getDriverLicense().trim().isEmpty();
            return ok ? 1 : 0;
        }).sum();

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
