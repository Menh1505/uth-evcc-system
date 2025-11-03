package com.evcc.expense.enums;

/**
 * Enum đại diện cho các loại chi phí liên quan đến xe
 */
public enum ExpenseType {
    CHARGING,           // Phí sạc điện
    MAINTENANCE,        // Bảo dưỡng xe
    INSURANCE,          // Bảo hiểm
    REGISTRATION,       // Đăng kiểm
    CLEANING,           // Vệ sinh xe
    PARKING,            // Phí đỗ xe
    TOLLS,              // Phí cầu đường
    REPAIRS,            // Sửa chữa
    ACCESSORIES,        // Phụ kiện
    FUEL_ALTERNATIVE,   // Nhiên liệu thay thế (nếu có)
    TAXES,              // Thuế phí
    ROADSIDE_ASSISTANCE, // Cứu hộ
    OTHER               // Chi phí khác
}