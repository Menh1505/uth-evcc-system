package com.evcc.notification.enums;

/**
 * Enum đại diện cho loại thông báo
 */
public enum NotificationType {
    // Booking related
    BOOKING_CONFIRMED,          // Booking được xác nhận
    BOOKING_CANCELLED,          // Booking bị hủy
    BOOKING_REMINDER,           // Nhắc nhở trước giờ sử dụng
    BOOKING_STARTED,            // Bắt đầu sử dụng xe
    BOOKING_COMPLETED,          // Hoàn thành chuyến đi
    BOOKING_OVERDUE,            // Quá giờ trả xe
    
    // Payment related
    PAYMENT_DUE,                // Hóa đơn đến hạn thanh toán
    PAYMENT_OVERDUE,            // Hóa đơn quá hạn
    PAYMENT_COMPLETED,          // Thanh toán thành công
    PAYMENT_FAILED,             // Thanh toán thất bại
    EXPENSE_ALLOCATED,          // Chi phí được phân bổ
    
    // Contract related
    CONTRACT_CREATED,           // Hợp đồng được tạo
    CONTRACT_SIGNED,            // Hợp đồng được ký
    CONTRACT_ACTIVATED,         // Hợp đồng có hiệu lực
    OWNERSHIP_CHANGED,          // Thay đổi tỉ lệ sở hữu
    
    // Vehicle related
    VEHICLE_MAINTENANCE,        // Xe cần bảo dưỡng
    VEHICLE_INSPECTION,         // Xe cần đăng kiểm
    VEHICLE_LOW_BATTERY,        // Pin xe thấp
    VEHICLE_ISSUE,              // Sự cố xe
    
    // System related
    SYSTEM_MAINTENANCE,         // Bảo trì hệ thống
    PRIORITY_UPDATED,           // Cập nhật độ ưu tiên
    GENERAL_ANNOUNCEMENT,       // Thông báo chung
    
    // Penalty related
    LATE_PENALTY,               // Phạt trễ giờ
    DAMAGE_REPORTED,            // Báo cáo hư hỏng
    VIOLATION_WARNING,          // Cảnh báo vi phạm
    
    OTHER                       // Loại khác
}