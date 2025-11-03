package com.evcc.expense.enums;

/**
 * Enum đại diện cho phương thức phân bổ chi phí
 */
public enum AllocationMethod {
    OWNERSHIP_PERCENTAGE,   // Chia theo tỉ lệ sở hữu
    USAGE_BASED,           // Chia theo mức độ sử dụng thực tế
    EQUAL_SPLIT,           // Chia đều cho tất cả thành viên
    FIXED_AMOUNT,          // Số tiền cố định cho từng người
    CUSTOM                 // Phân bổ tùy chỉnh
}