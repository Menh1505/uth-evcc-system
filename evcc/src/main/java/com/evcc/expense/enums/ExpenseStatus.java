package com.evcc.expense.enums;

/**
 * Enum đại diện cho trạng thái của khoản chi phí
 */
public enum ExpenseStatus {
    DRAFT,              // Bản nháp - đang soạn thảo
    PENDING_APPROVAL,   // Chờ phê duyệt
    APPROVED,           // Đã phê duyệt - chờ thanh toán
    PARTIALLY_PAID,     // Đã thanh toán một phần
    FULLY_PAID,         // Đã thanh toán đầy đủ
    OVERDUE,            // Quá hạn thanh toán
    CANCELLED,          // Đã hủy
    DISPUTED            // Đang tranh chấp
}