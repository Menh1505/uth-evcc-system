package com.evcc.expense.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import com.evcc.contract.entity.Contract;
import com.evcc.expense.enums.AllocationMethod;
import com.evcc.expense.enums.ExpenseStatus;
import com.evcc.expense.enums.ExpenseType;
import com.evcc.user.entity.User;
import com.evcc.vehicle.entity.Vehicle;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity đại diện cho một khoản chi phí liên quan đến xe
 * Có thể được phân bổ cho các thành viên theo nhiều cách khác nhau
 */
@Entity
@Table(name = "vehicle_expenses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleExpense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Mã chi phí duy nhất
     */
    @Column(name = "expense_reference", unique = true, nullable = false, length = 50)
    private String expenseReference;

    /**
     * Hợp đồng liên quan
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    /**
     * Xe phát sinh chi phí
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    /**
     * Người tạo khoản chi phí
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    /**
     * Tiêu đề khoản chi phí
     */
    @Column(nullable = false, length = 255)
    private String title;

    /**
     * Mô tả chi tiết
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Loại chi phí
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "expense_type", nullable = false)
    private ExpenseType expenseType;

    /**
     * Số tiền tổng cộng
     */
    @Column(name = "total_amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    /**
     * Ngày phát sinh chi phí
     */
    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate;

    /**
     * Ngày đến hạn thanh toán
     */
    @Column(name = "due_date")
    private LocalDate dueDate;

    /**
     * Trạng thái chi phí
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExpenseStatus status;

    /**
     * Phương thức phân bổ
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "allocation_method", nullable = false)
    private AllocationMethod allocationMethod;

    /**
     * Thông tin nhà cung cấp/đối tác
     */
    @Column(name = "vendor_name", length = 255)
    private String vendorName;

    /**
     * Số hóa đơn/biên lai
     */
    @Column(name = "invoice_number", length = 100)
    private String invoiceNumber;

    /**
     * Đường dẫn file đính kèm (hóa đơn, biên lai...)
     */
    @Column(name = "attachment_path", length = 500)
    private String attachmentPath;

    /**
     * Có phải chi phí định kỳ không
     */
    @Column(name = "is_recurring")
    @Builder.Default
    private Boolean isRecurring = false;

    /**
     * Tần suất lặp lại (nếu là chi phí định kỳ)
     * VD: MONTHLY, QUARTERLY, YEARLY
     */
    @Column(name = "recurrence_pattern", length = 50)
    private String recurrencePattern;

    /**
     * Ngày kết thúc chu kỳ lặp lại
     */
    @Column(name = "recurrence_end_date")
    private LocalDate recurrenceEndDate;

    /**
     * Có cần phê duyệt không
     */
    @Column(name = "requires_approval")
    @Builder.Default
    private Boolean requiresApproval = true;

    /**
     * Người phê duyệt
     */
    @Column(name = "approved_by")
    private UUID approvedBy;

    /**
     * Thời gian phê duyệt
     */
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    /**
     * Ghi chú phê duyệt
     */
    @Column(name = "approval_notes", columnDefinition = "TEXT")
    private String approvalNotes;

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
     * Có thể phân bổ dựa trên usage không
     */
    @Column(name = "usage_allocable")
    @Builder.Default
    private Boolean usageAllocable = true;

    /**
     * Khoảng thời gian usage để tính phân bổ (ngày)
     */
    @Column(name = "usage_period_days")
    @Builder.Default
    private Integer usagePeriodDays = 30;

    /**
     * Tags để phân loại (JSON array)
     */
    @Column(columnDefinition = "TEXT")
    private String tags;

    /**
     * Ghi chú
     */
    @Column(columnDefinition = "TEXT")
    private String notes;

    /**
     * Danh sách phân bổ chi phí cho từng thành viên
     */
    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<ExpenseAllocation> allocations;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        
        // Tự động tạo expense reference nếu chưa có
        if (this.expenseReference == null || this.expenseReference.isEmpty()) {
            this.expenseReference = generateExpenseReference();
        }
        
        // Mặc định là DRAFT nếu chưa có status
        if (this.status == null) {
            this.status = ExpenseStatus.DRAFT;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Tạo mã chi phí tự động theo format: EXP-YYYYMMDD-XXXXX
     */
    private String generateExpenseReference() {
        LocalDateTime now = LocalDateTime.now();
        String dateStr = String.format("%04d%02d%02d", 
            now.getYear(), now.getMonthValue(), now.getDayOfMonth());
        int randomSuffix = (int)(Math.random() * 100000);
        return "EXP-" + dateStr + "-" + String.format("%05d", randomSuffix);
    }

    /**
     * Tính số tiền còn nợ
     */
    public BigDecimal getOutstandingAmount() {
        return totalAmount.subtract(paidAmount);
    }

    /**
     * Tính tỉ lệ thanh toán (%)
     */
    public BigDecimal getPaymentPercentage() {
        if (totalAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return paidAmount.divide(totalAmount, 4, java.math.RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100));
    }

    /**
     * Kiểm tra có phải chi phí quá hạn không
     */
    public boolean isOverdue() {
        return dueDate != null && LocalDate.now().isAfter(dueDate) && 
               getOutstandingAmount().compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Kiểm tra có thể hủy không
     */
    public boolean isCancellable() {
        return status == ExpenseStatus.DRAFT || status == ExpenseStatus.PENDING_APPROVAL;
    }

    /**
     * Kiểm tra có thể sửa không
     */
    public boolean isEditable() {
        return status == ExpenseStatus.DRAFT;
    }

    /**
     * Kiểm tra đã thanh toán đầy đủ chưa
     */
    public boolean isFullyPaid() {
        return status == ExpenseStatus.FULLY_PAID || 
               getOutstandingAmount().compareTo(new BigDecimal("0.01")) < 0;
    }

    /**
     * Tính số ngày đến hạn thanh toán
     */
    public long getDaysUntilDue() {
        if (dueDate == null) return -1;
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
    }
}