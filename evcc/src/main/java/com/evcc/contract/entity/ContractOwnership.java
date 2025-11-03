package com.evcc.contract.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.evcc.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
 * Entity đại diện cho quyền sở hữu của từng thành viên trong hợp đồng
 * Ghi lại tỉ lệ sở hữu và số tiền đóng góp của mỗi người
 */
@Entity
@Table(name = "contract_ownerships", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"contract_id", "user_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractOwnership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Hợp đồng mà quyền sở hữu này thuộc về
     */
    @ManyToOne
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    /**
     * Thành viên sở hữu
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Tỉ lệ sở hữu (từ 0.01 đến 100.00)
     * Ví dụ: 25.50 có nghĩa là sở hữu 25.5%
     */
    @Column(name = "ownership_percentage", precision = 5, scale = 2, nullable = false)
    private BigDecimal ownershipPercentage;

    /**
     * Số tiền đã đóng góp
     */
    @Column(name = "contribution_amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal contributionAmount;

    /**
     * Ngày xác nhận đóng góp
     */
    @Column(name = "contribution_date")
    private LocalDateTime contributionDate;

    /**
     * Trạng thái thanh toán
     */
    @Column(name = "payment_status", length = 20, nullable = false)
    @Builder.Default
    private String paymentStatus = "PENDING"; // PENDING, PARTIAL, COMPLETED

    /**
     * Ghi chú về khoản đóng góp
     */
    @Column(columnDefinition = "TEXT")
    private String notes;

    /**
     * Có được quyền sử dụng xe hay không (dựa trên thanh toán và tỉ lệ)
     */
    @Column(name = "usage_eligible")
    @Builder.Default
    private Boolean usageEligible = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        
        if (this.contributionDate == null) {
            this.contributionDate = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Kiểm tra xem quyền sở hữu này có hợp lệ không
     */
    public boolean isValid() {
        return ownershipPercentage != null && 
               ownershipPercentage.compareTo(BigDecimal.ZERO) > 0 &&
               ownershipPercentage.compareTo(new BigDecimal("100.00")) <= 0 &&
               contributionAmount != null &&
               contributionAmount.compareTo(BigDecimal.ZERO) >= 0;
    }

    /**
     * Kiểm tra xem đã thanh toán đầy đủ chưa
     */
    public boolean isFullyPaid() {
        return "COMPLETED".equals(paymentStatus);
    }
}