package com.evcc.payment.enums;

/**
 * Enum đại diện cho loại giao dịch thanh toán
 */
public enum PaymentType {
    EXPENSE_PAYMENT,        // Thanh toán chi phí
    CONTRACT_CONTRIBUTION,  // Đóng góp mua xe theo hợp đồng
    PENALTY_FEE,           // Phí phạt (trễ hạn, hủy booking...)
    DEPOSIT,               // Tiền đặt cọc
    REFUND,                // Hoàn tiền
    ADJUSTMENT,            // Điều chỉnh số dư
    OTHER                  // Loại khác
}