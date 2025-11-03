package com.evcc.contract.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.evcc.contract.enums.ContractStatus;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO để cập nhật thông tin hợp đồng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateContractRequest {

    @NotBlank(message = "Tiêu đề hợp đồng không được để trống")
    private String title;

    private String description;

    private Long vehicleId;

    @Positive(message = "Giá thỏa thuận phải lớn hơn 0")
    private BigDecimal agreedPrice;

    private LocalDate signingDate;

    private LocalDate effectiveDate;

    private LocalDate expiryDate;

    private ContractStatus status;

    private String termsAndConditions;

    private String notes;

    /**
     * Danh sách cập nhật quyền sở hữu (tùy chọn)
     */
    @Valid
    private List<OwnershipUpdateRequest> ownerships;

    /**
     * DTO lồng nhau cho cập nhật thông tin quyền sở hữu
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OwnershipUpdateRequest {
        
        private Long id; // ID của ownership (null nếu tạo mới)
        
        private UUID userId;

        @Positive(message = "Tỉ lệ sở hữu phải lớn hơn 0")
        private BigDecimal ownershipPercentage;

        private BigDecimal contributionAmount;

        private String paymentStatus; // PENDING, PARTIAL, COMPLETED

        private Boolean usageEligible;

        private String notes;
    }
}