package com.evcc.user.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evcc.user.entity.Role;
import com.evcc.user.entity.User;
import com.evcc.user.repository.RoleRepository;
import com.evcc.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service để khởi tạo dữ liệu admin và role mặc định
 */
@Service
@RequiredArgsConstructor
public class AdminInitializationService {

    private static final Logger logger = LoggerFactory.getLogger(AdminInitializationService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // Constants cho admin account
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_USER = "USER";

    /**
     * Khởi tạo roles và admin user nếu chưa có
     */
    @Transactional
    public void initializeAdminData() {
        logger.info("Bắt đầu khởi tạo dữ liệu admin...");

        try {
            // 1. Tạo các role mặc định
            initializeRoles();

            // 2. Tạo admin user nếu chưa có
            initializeAdminUser();

            logger.info("Hoàn thành khởi tạo dữ liệu admin");

        } catch (Exception e) {
            logger.error("Lỗi khi khởi tạo dữ liệu admin: {}", e.getMessage(), e);
            throw new IllegalStateException("Không thể khởi tạo dữ liệu admin", e);
        }
    }

    /**
     * Khởi tạo các role mặc định
     */
    private void initializeRoles() {
        logger.info("Kiểm tra và tạo roles mặc định...");

        // Tạo role ADMIN nếu chưa có
        if (!roleRepository.existsByName(ROLE_ADMIN)) {
            Role adminRole = Role.builder()
                    .name(ROLE_ADMIN)
                    .build();
            roleRepository.save(adminRole);
            logger.info("Đã tạo role: {}", ROLE_ADMIN);
        } else {
            logger.info("Role {} đã tồn tại", ROLE_ADMIN);
        }

        // Tạo role USER nếu chưa có
        if (!roleRepository.existsByName(ROLE_USER)) {
            Role userRole = Role.builder()
                    .name(ROLE_USER)
                    .build();
            roleRepository.save(userRole);
            logger.info("Đã tạo role: {}", ROLE_USER);
        } else {
            logger.info("Role {} đã tồn tại", ROLE_USER);
        }
    }

    /**
     * Khởi tạo admin user nếu chưa có
     */
    private void initializeAdminUser() {
        logger.info("Kiểm tra và tạo admin user...");

        Optional<User> existingAdmin = userRepository.findByUsername(ADMIN_USERNAME);

        if (existingAdmin.isEmpty()) {
            // Tạo admin user mới
            createAdminUser();
        } else {
            // Kiểm tra admin user có role ADMIN chưa
            User admin = existingAdmin.get();
            ensureAdminHasAdminRole(admin);
            logger.info("Admin user '{}' đã tồn tại", ADMIN_USERNAME);
        }
    }

    /**
     * Tạo admin user mới
     */
    private void createAdminUser() {
        logger.info("Tạo admin user mới...");

        // Lấy roles
        Role adminRole = roleRepository.findByName(ROLE_ADMIN)
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy role ADMIN"));
        Role userRole = roleRepository.findByName(ROLE_USER)
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy role USER"));

        // Tạo user admin
        User admin = new User(ADMIN_USERNAME, passwordEncoder.encode(ADMIN_PASSWORD));
        admin.setIsVerified(true); // Admin được verify tự động
        admin.setCitizenId("000000000000"); // ID mặc định cho admin
        admin.setDriverLicense("ADMIN-LICENSE"); // License mặc định cho admin

        // Gán roles cho admin (cả ADMIN và USER)
        Set<Role> roles = new HashSet<>();
        roles.add(adminRole);
        roles.add(userRole);
        admin.setRoles(roles);

        // Lưu admin user
        userRepository.save(admin);

        logger.info("Đã tạo admin user: username='{}', password='{}', verified=true",
                ADMIN_USERNAME, ADMIN_PASSWORD);
        logger.warn("⚠️  QUAN TRỌNG: Hãy đổi mật khẩu admin sau khi đăng nhập lần đầu!");
    }

    /**
     * Đảm bảo admin user có role ADMIN
     */
    private void ensureAdminHasAdminRole(User admin) {
        Role adminRole = roleRepository.findByName(ROLE_ADMIN)
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy role ADMIN"));

        if (admin.getRoles() == null) {
            admin.setRoles(new HashSet<>());
        }

        // Kiểm tra xem admin đã có role ADMIN chưa
        boolean hasAdminRole = admin.getRoles().stream()
                .anyMatch(role -> ROLE_ADMIN.equals(role.getName()));

        if (!hasAdminRole) {
            logger.info("Thêm role ADMIN cho user '{}'", admin.getUsername());
            admin.getRoles().add(adminRole);
            userRepository.save(admin);
        }

        // Đảm bảo admin được verify
        if (!admin.getIsVerified()) {
            logger.info("Verify admin user '{}'", admin.getUsername());
            admin.setIsVerified(true);
            userRepository.save(admin);
        }
    }

    /**
     * Kiểm tra xem admin đã tồn tại chưa
     */
    public boolean isAdminExists() {
        return userRepository.findByUsername(ADMIN_USERNAME)
                .map(admin -> admin.getRoles().stream()
                .anyMatch(role -> ROLE_ADMIN.equals(role.getName())))
                .orElse(false);
    }

    /**
     * Reset mật khẩu admin về mặc định (dùng cho development)
     */
    @Transactional
    public void resetAdminPassword() {
        logger.warn("Reset mật khẩu admin về mặc định...");

        Optional<User> adminOpt = userRepository.findByUsername(ADMIN_USERNAME);
        if (adminOpt.isPresent()) {
            User admin = adminOpt.get();
            admin.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
            userRepository.save(admin);
            logger.info("Đã reset mật khẩu admin thành: {}", ADMIN_PASSWORD);
        } else {
            logger.error("Không tìm thấy admin user để reset mật khẩu");
        }
    }

    /**
     * Lấy thông tin admin hiện tại
     */
    public Optional<User> getAdminUser() {
        return userRepository.findByUsernameWithRoles(ADMIN_USERNAME);
    }
}
