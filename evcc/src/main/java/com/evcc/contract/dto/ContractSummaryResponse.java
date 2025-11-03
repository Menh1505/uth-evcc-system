package com.evcc.contract.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.evcc.contract.enums.ContractStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO trả về danh sách hợp đồng (thông tin tóm tắt)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractSummaryResponse {

    private Long id;
    private String contractNumber;
    private String title;
    private String groupName;
    private String vehicleName;
    private String vehicleLicensePlate;
    private BigDecimal agreedPrice;
    private LocalDate signingDate;
    private ContractStatus status;
    private int totalOwners;
    private BigDecimal totalContributed;
    private BigDecimal contributionPercentage; // % đã đóng góp so với giá xe
}