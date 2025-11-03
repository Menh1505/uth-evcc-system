package com.evcc.contract.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.evcc.contract.enums.ContractStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO trả về thông tin chi tiết hợp đồng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractResponse {

    private Long id;
    private String contractNumber;
    private String title;
    private String description;
    private GroupInfo group;
    private VehicleInfo vehicle;
    private BigDecimal agreedPrice;
    private LocalDate signingDate;
    private LocalDate effectiveDate;
    private LocalDate expiryDate;
    private ContractStatus status;
    private String termsAndConditions;
    private String notes;
    private List<OwnershipInfo> ownerships;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Thông tin nhóm đơn giản
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GroupInfo {
        private Long id;
        private String name;
        private String description;
    }

    /**
     * Thông tin xe đơn giản
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VehicleInfo {
        private Long id;
        private String name;
        private String licensePlate;
        private String make;
        private String model;
        private Integer year;
    }

    /**
     * Thông tin quyền sở hữu
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OwnershipInfo {
        private Long id;
        private UserInfo user;
        private BigDecimal ownershipPercentage;
        private BigDecimal contributionAmount;
        private LocalDateTime contributionDate;
        private String paymentStatus;
        private Boolean usageEligible;
        private String notes;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    /**
     * Thông tin người dùng đơn giản
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserInfo {
        private UUID id;
        private String username;
        private String email;
        private String firstName;
        private String lastName;
    }
}