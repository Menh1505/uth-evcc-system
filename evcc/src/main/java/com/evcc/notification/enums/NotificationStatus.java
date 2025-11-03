package com.evcc.notification.enums;

/**
 * Enum đại diện cho trạng thái thông báo
 */
public enum NotificationStatus {
    PENDING,        // Chờ gửi
    SENT,           // Đã gửi
    DELIVERED,      // Đã nhận
    READ,           // Đã đọc
    FAILED,         // Gửi thất bại
    CANCELLED       // Đã hủy
}