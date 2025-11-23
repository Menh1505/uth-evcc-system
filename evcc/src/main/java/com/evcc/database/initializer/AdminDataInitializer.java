package com.evcc.database.initializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.evcc.user.service.AdminInitializationService;

import lombok.RequiredArgsConstructor;

/**
 * Component chạy khi khởi động ứng dụng để khởi tạo dữ liệu admin
 */
@Component
@Order(1) // Đảm bảo chạy đầu tiên
@RequiredArgsConstructor
public class AdminDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AdminDataInitializer.class);

    private final AdminInitializationService adminInitializationService;

    @Override
    public void run(String... args) throws Exception {
        logger.info("=== KHỞI ĐỘNG HỆ THỐNG EVCC ===");
        logger.info("Kiểm tra và khởi tạo dữ liệu admin...");

        try {
            adminInitializationService.initializeAdminData();

            // Log thông tin admin sau khi khởi tạo
            logAdminInfo();

        } catch (Exception e) {
            logger.error("❌ Lỗi khi khởi tạo dữ liệu admin: {}", e.getMessage(), e);
            // Không throw exception để không làm crash ứng dụng
        }

        logger.info("=== HỆ THỐNG EVCC ĐÃ KHỞI ĐỘNG HOÀN TẤT ===");
    }

    /**
     * Log thông tin admin account
     */
    private void logAdminInfo() {
        if (adminInitializationService.isAdminExists()) {
            logger.info("✅ Admin account đã sẵn sàng");
            logger.info("📋 Thông tin đăng nhập admin:");
            logger.info("   Username: admin");
            logger.info("   Password: admin");
            logger.info("   URL Admin: http://localhost:3000/admin");
            logger.warn("⚠️  QUAN TRỌNG: Hãy đổi mật khẩu admin sau khi đăng nhập lần đầu!");
        } else {
            logger.error("❌ Không thể tạo admin account");
        }
    }
}
