package com.evcc.payment.enums;

/**
 * Enum đại diện cho các phương thức thanh toán
 */
public enum PaymentMethod {
    E_WALLET,           // Ví điện tử (MoMo, ZaloPay, ShopeePay...)
    BANK_TRANSFER,      // Chuyển khoản ngân hàng
    CREDIT_CARD,        // Thẻ tín dụng
    DEBIT_CARD,         // Thẻ ghi nợ
    QR_CODE,            // Thanh toán QR code
    CASH,               // Tiền mặt
    CRYPTO,             // Tiền điện tử
    INSTALLMENT,        // Trả góp
    OTHER               // Phương thức khác
}