package com.evcc.contract.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.Set;

import com.evcc.contract.enums.ContractStatus;
import com.evcc.group.entity.Group;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity đại diện cho hợp đồng mua xe của nhóm
 * Một hợp đồng thuộc về một nhóm và liên kết với một xe
 */
@Entity
@Table(name = "contracts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Mã hợp đồng duy nhất
     */
    @Column(name = "contract_number", unique = true, nullable = false, length = 50)
    private String contractNumber;

    /**
     * Tiêu đề hợp đồng
     */
    @Column(nullable = false, length = 255)
    private String title;

    /**
     * Mô tả chi tiết hợp đồng
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Nhóm sở hữu hợp đồng
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    /**
     * Xe được mua theo hợp đồng (one-to-one)
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    /**
     * Giá xe được thỏa thuận trong hợp đồng
     */
    @Column(name = "agreed_price", precision = 19, scale = 2, nullable = false)
    private BigDecimal agreedPrice;

    /**
     * Ngày ký hợp đồng
     */
    @Column(name = "signing_date")
    private LocalDate signingDate;

    /**
     * Ngày có hiệu lực
     */
    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    /**
     * Ngày kết thúc (tùy chọn)
     */
    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    /**
     * Trạng thái hợp đồng
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContractStatus status;

    /**
     * Điều khoản và điều kiện
     */
    @Column(name = "terms_and_conditions", columnDefinition = "TEXT")
    private String termsAndConditions;

    /**
     * Ghi chú
     */
    @Column(columnDefinition = "TEXT")
    private String notes;

    /**
     * Danh sách quyền sở hữu của các thành viên
     */
    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<ContractOwnership> ownerships;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        
        // Tự động tạo mã hợp đồng nếu chưa có
        if (this.contractNumber == null || this.contractNumber.isEmpty()) {
            this.contractNumber = generateContractNumber();
        }
        
        // Mặc định là DRAFT nếu chưa có status
        if (this.status == null) {
            this.status = ContractStatus.DRAFT;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Tạo mã hợp đồng tự động theo format: CONTRACT-YYYYMMDD-XXXXX
     */
    private String generateContractNumber() {
        LocalDateTime now = LocalDateTime.now();
        String dateStr = String.format("%04d%02d%02d", 
            now.getYear(), now.getMonthValue(), now.getDayOfMonth());
        Random random = new Random();
        String randomSuffix = String.format("%05d", random.nextInt(100000));
        return "CONTRACT-" + dateStr + "-" + randomSuffix;
    }
}