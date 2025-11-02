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

        User savedUser = userRepository.save(user);
        return getUserProfile(savedUser.getId());
    }

    /**
     * Xác minh tài khoản user (chỉ admin mới được gọi)
     */
    public UserProfileResponse verifyUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy user với ID: " + userId));

        user.setIsVerified(true);
        User savedUser = userRepository.save(user);
        return getUserProfile(savedUser.getId());
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
}
