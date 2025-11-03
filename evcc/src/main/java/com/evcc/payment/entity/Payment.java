package com.evcc.payment.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.evcc.expense.entity.ExpenseAllocation;
import com.evcc.payment.enums.PaymentMethod;
import com.evcc.payment.enums.PaymentStatus;
import com.evcc.payment.enums.PaymentType;
import com.evcc.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity đại diện cho một giao dịch thanh toán
 */
@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Mã giao dịch duy nhất
     */
    @Column(name = "transaction_id", unique = true, nullable = false, length = 100)
    private String transactionId;

    /**
     * Mã tham chiếu từ hệ thống thanh toán bên ngoài
     */
    @Column(name = "external_reference", length = 255)
    private String externalReference;

    /**
     * Người thanh toán
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payer_id", nullable = false)
    private User payer;

    /**
     * Khoản phân bổ chi phí được thanh toán (nếu có)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_allocation_id")
    private ExpenseAllocation expenseAllocation;

    /**
     * Loại thanh toán
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false)
    private PaymentType paymentType;

    /**
     * Phương thức thanh toán
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    /**
     * Số tiền thanh toán
     */
    @Column(name = "amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal amount;

    /**
     * Đơn vị tiền tệ (VND, USD...)
     */
    @Column(name = "currency", length = 3, nullable = false)
    @Builder.Default
    private String currency = "VND";

    /**
     * Trạng thái thanh toán
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    /**
     * Mô tả giao dịch
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Thời gian khởi tạo giao dịch
     */
    @Column(name = "initiated_at", nullable = false)
    private LocalDateTime initiatedAt;

    /**
     * Thời gian hoàn thành giao dịch
     */
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    /**
     * Thời gian hết hạn giao dịch
     */
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    /**
     * Thông tin gateway thanh toán
     */
    @Column(name = "gateway_name", length = 100)
    private String gatewayName;

    /**
     * Response từ gateway (JSON)
     */
    @Column(name = "gateway_response", columnDefinition = "TEXT")
    private String gatewayResponse;

    /**
     * Phí giao dịch
     */
    @Column(name = "transaction_fee", precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal transactionFee = BigDecimal.ZERO;

    /**
     * Số tiền thực nhận (sau khi trừ phí)
     */
    @Column(name = "net_amount", precision = 19, scale = 2)
    private BigDecimal netAmount;

    /**
     * Mã lỗi (nếu có)
     */
    @Column(name = "error_code", length = 50)
    private String errorCode;

    /**
     * Thông báo lỗi
     */
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    /**
     * Có thể hoàn tiền không
     */
    @Column(name = "refundable")
    @Builder.Default
    private Boolean refundable = true;

    /**
     * Số tiền đã hoàn
     */
    @Column(name = "refunded_amount", precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal refundedAmount = BigDecimal.ZERO;

    /**
     * Thời gian hoàn tiền cuối cùng
     */
    @Column(name = "last_refund_at")
    private LocalDateTime lastRefundAt;

    /**
     * IP address của người thanh toán
     */
    @Column(name = "payer_ip", length = 45)
    private String payerIp;

    /**
     * User agent của thiết bị thanh toán
     */
    @Column(name = "user_agent", length = 500)
    private String userAgent;

    /**
     * Metadata bổ sung (JSON)
     */
    @Column(columnDefinition = "TEXT")
    private String metadata;

    /**
     * Ghi chú từ người thanh toán
     */
    @Column(name = "payer_notes", columnDefinition = "TEXT")
    private String payerNotes;

    /**
     * Ghi chú từ admin/system
     */
    @Column(name = "admin_notes", columnDefinition = "TEXT")
    private String adminNotes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        this.initiatedAt = now;
        
        // Tự động tạo transaction ID nếu chưa có
        if (this.transactionId == null || this.transactionId.isEmpty()) {
            this.transactionId = generateTransactionId();
        }
        
        // Mặc định là PENDING nếu chưa có status
        if (this.status == null) {
            this.status = PaymentStatus.PENDING;
        }
        
        // Tính net amount
        if (this.netAmount == null) {
            this.netAmount = this.amount.subtract(this.transactionFee);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        
        // Cập nhật completed_at khi status chuyển sang COMPLETED
        if (this.status == PaymentStatus.COMPLETED && this.completedAt == null) {
            this.completedAt = LocalDateTime.now();
        }
    }

    /**
     * Tạo mã giao dịch tự động theo format: PAY-YYYYMMDD-HHMMSS-XXXXX
     */
    private String generateTransactionId() {
        LocalDateTime now = LocalDateTime.now();
        String dateTimeStr = String.format("%04d%02d%02d-%02d%02d%02d",
            now.getYear(), now.getMonthValue(), now.getDayOfMonth(),
            now.getHour(), now.getMinute(), now.getSecond());
        int randomSuffix = (int)(Math.random() * 100000);
        return "PAY-" + dateTimeStr + "-" + String.format("%05d", randomSuffix);
    }

    /**
     * Tính số tiền có thể hoàn
     */
    public BigDecimal getRefundableAmount() {
        if (!refundable || status != PaymentStatus.COMPLETED) {
            return BigDecimal.ZERO;
        }
        return amount.subtract(refundedAmount);
    }

    /**
     * Kiểm tra giao dịch đã hoàn thành chưa
     */
    public boolean isCompleted() {
        return status == PaymentStatus.COMPLETED;
    }

    /**
     * Kiểm tra giao dịch có thất bại không
     */
    public boolean isFailed() {
        return status == PaymentStatus.FAILED || status == PaymentStatus.EXPIRED;
    }

    /**
     * Kiểm tra có thể hủy giao dịch không
     */
    public boolean isCancellable() {
        return status == PaymentStatus.PENDING || status == PaymentStatus.PROCESSING;
    }

    /**
     * Kiểm tra giao dịch có hết hạn không
     */
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * Tính thời gian còn lại trước khi hết hạn (giây)
     */
    public long getSecondsUntilExpiry() {
        if (expiresAt == null) return -1;
        return java.time.Duration.between(LocalDateTime.now(), expiresAt).getSeconds();
    }

    /**
     * Đánh dấu giao dịch hoàn thành
     */
    public void markAsCompleted(String gatewayResponse) {
        this.status = PaymentStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.gatewayResponse = gatewayResponse;
        
        // Cập nhật expense allocation nếu có
        if (this.expenseAllocation != null) {
            this.expenseAllocation.makePayment(this.amount);
        }
    }

    /**
     * Đánh dấu giao dịch thất bại
     */
    public void markAsFailed(String errorCode, String errorMessage) {
        this.status = PaymentStatus.FAILED;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /**
     * Thực hiện hoàn tiền một phần
     */
    public void processRefund(BigDecimal refundAmount) {
        if (refundAmount.compareTo(getRefundableAmount()) > 0) {
            throw new IllegalArgumentException("Số tiền hoàn vượt quá số tiền có thể hoàn");
        }
        
        this.refundedAmount = this.refundedAmount.add(refundAmount);
        this.lastRefundAt = LocalDateTime.now();
        
        // Cập nhật status
        if (this.refundedAmount.compareTo(this.amount) >= 0) {
            this.status = PaymentStatus.REFUNDED;
        } else {
            this.status = PaymentStatus.PARTIALLY_REFUNDED;
        }
    }
}