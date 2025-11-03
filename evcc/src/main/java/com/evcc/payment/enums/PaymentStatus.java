package com.evcc.payment.enums;

/**
 * Enum đại diện cho trạng thái của giao dịch thanh toán
 */
public enum PaymentStatus {
    PENDING,            // Đang chờ xử lý
    PROCESSING,         // Đang xử lý
    COMPLETED,          // Thành công
    FAILED,             // Thất bại
    CANCELLED,          // Đã hủy
    REFUNDED,           // Đã hoàn tiền
    PARTIALLY_REFUNDED, // Hoàn tiền một phần
    EXPIRED,            // Hết hạn
    DISPUTED            // Đang tranh chấp
}