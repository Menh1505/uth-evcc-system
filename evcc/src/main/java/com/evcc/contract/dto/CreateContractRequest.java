package com.evcc.contract.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO để tạo mới hợp đồng mua xe
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateContractRequest {

    @NotBlank(message = "Tiêu đề hợp đồng không được để trống")
    private String title;

    private String description;

    @NotNull(message = "ID nhóm không được để trống")
    @Positive(message = "ID nhóm phải là số dương")
    private Long groupId;

    private Long vehicleId; // Tùy chọn - có thể gán xe sau

    @NotNull(message = "Giá thỏa thuận không được để trống")
    @Positive(message = "Giá thỏa thuận phải lớn hơn 0")
    private BigDecimal agreedPrice;

    private LocalDate signingDate;

    private LocalDate effectiveDate;

    private LocalDate expiryDate;

    private String termsAndConditions;

    private String notes;

    /**
     * Danh sách quyền sở hữu của các thành viên
     */
    @NotEmpty(message = "Danh sách quyền sở hữu không được để trống")
    @Valid
    private List<OwnershipRequest> ownerships;

    /**
     * DTO lồng nhau cho thông tin quyền sở hữu
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OwnershipRequest {
        
        @NotNull(message = "ID người dùng không được để trống")
        private UUID userId;

        @NotNull(message = "Tỉ lệ sở hữu không được để trống")
        @Positive(message = "Tỉ lệ sở hữu phải lớn hơn 0")
        private BigDecimal ownershipPercentage;

        @NotNull(message = "Số tiền đóng góp không được để trống")
        private BigDecimal contributionAmount; // Có thể = 0 nếu chưa đóng

        private String notes;
    }
}