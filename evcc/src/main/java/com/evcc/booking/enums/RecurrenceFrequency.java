package com.evcc.booking.enums;

/**
 * Enum đại diện cho tần suất lặp lại của đặt lịch định kỳ
 */
public enum RecurrenceFrequency {
    DAILY,          // Hàng ngày
    WEEKLY,         // Hàng tuần  
    MONTHLY,        // Hàng tháng
    CUSTOM          // Tùy chỉnh (có thể định nghĩa pattern riêng)
}