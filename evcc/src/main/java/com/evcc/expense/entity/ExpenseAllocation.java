package com.evcc.expense.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.evcc.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity đại diện cho việc phân bổ chi phí cho từng thành viên
 */
@Entity
@Table(name = "expense_allocations", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"expense_id", "user_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Khoản chi phí được phân bổ
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_id", nullable = false)
    private VehicleExpense expense;

    /**
     * Thành viên được phân bổ chi phí
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Số tiền được phân bổ cho thành viên này
     */
    @Column(name = "allocated_amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal allocatedAmount;

    /**
     * Tỉ lệ phân bổ (%) tại thời điểm tính toán
     */
    @Column(name = "allocation_percentage", precision = 5, scale = 2, nullable = false)
    private BigDecimal allocationPercentage;

    /**
     * Cơ sở để tính phân bổ (ownership %, usage hours, equal split...)
     */
    @Column(name = "allocation_basis", length = 100, nullable = false)
    private String allocationBasis;

    /**
     * Số liệu để tính phân bổ (VD: 25.5% ownership, 45 hours usage...)
     */
    @Column(name = "basis_value", precision = 10, scale = 2)
    private BigDecimal basisValue;

    /**
     * Số tiền đã thanh toán
     */
    @Column(name = "paid_amount", precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal paidAmount = BigDecimal.ZERO;

    /**
     * Ngày thanh toán cuối cùng
     */
    @Column(name = "last_payment_date")
    private LocalDateTime lastPaymentDate;

    /**
     * Trạng thái thanh toán: PENDING, PARTIAL, COMPLETED, OVERDUE
     */
    @Column(name = "payment_status", length = 20, nullable = false)
    @Builder.Default
    private String paymentStatus = "PENDING";

    /**
     * Có được miễn thanh toán không (special case)
     */
    @Column(name = "is_exempted")
    @Builder.Default
    private Boolean isExempted = false;

    /**
     * Lý do miễn thanh toán
     */
    @Column(name = "exemption_reason", columnDefinition = "TEXT")
    private String exemptionReason;

    /**
     * Thứ tự ưu tiên thanh toán (1 = cao nhất)
     */
    @Column(name = "payment_priority")
    @Builder.Default
    private Integer paymentPriority = 5;

    /**
     * Ghi chú về phân bổ
     */
    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Tính số tiền còn nợ
     */
    public BigDecimal getOutstandingAmount() {
        if (isExempted) return BigDecimal.ZERO;
        return allocatedAmount.subtract(paidAmount);
    }

    /**
     * Tính tỉ lệ thanh toán (%)
     */
    public BigDecimal getPaymentPercentage() {
        if (isExempted) return new BigDecimal(100);
        if (allocatedAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return paidAmount.divide(allocatedAmount, 4, java.math.RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100));
    }

    /**
     * Kiểm tra đã thanh toán đầy đủ chưa
     */
    public boolean isFullyPaid() {
        return isExempted || "COMPLETED".equals(paymentStatus) || 
               getOutstandingAmount().compareTo(new BigDecimal("0.01")) < 0;
    }

    /**
     * Kiểm tra có phải overdue không
     */
    public boolean isOverdue() {
        return "OVERDUE".equals(paymentStatus);
    }

    /**
     * Cập nhật trạng thái thanh toán dựa trên số tiền đã trả
     */
    public void updatePaymentStatus() {
        if (isExempted) {
            this.paymentStatus = "COMPLETED";
            return;
        }

        BigDecimal outstanding = getOutstandingAmount();
        if (outstanding.compareTo(new BigDecimal("0.01")) < 0) {
            this.paymentStatus = "COMPLETED";
        } else if (paidAmount.compareTo(BigDecimal.ZERO) > 0) {
            this.paymentStatus = "PARTIAL";
        } else {
            // Kiểm tra quá hạn dựa trên due date của expense
            if (expense != null && expense.getDaysUntilDue() < 0) {
                this.paymentStatus = "OVERDUE";
            } else {
                this.paymentStatus = "PENDING";
            }
        }
    }

    /**
     * Thực hiện thanh toán một phần
     */
    public void makePayment(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Số tiền thanh toán phải lớn hơn 0");
        }

        BigDecimal newPaidAmount = this.paidAmount.add(amount);
        if (newPaidAmount.compareTo(this.allocatedAmount) > 0) {
            throw new IllegalArgumentException("Số tiền thanh toán không thể vượt quá số tiền được phân bổ");
        }

        this.paidAmount = newPaidAmount;
        this.lastPaymentDate = LocalDateTime.now();
        updatePaymentStatus();
    }
}