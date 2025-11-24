package com.evcc.vehicle.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho proposal mua xe mới với voting
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehiclePurchaseProposalRequest {

    @NotBlank(message = "Tên xe không được để trống")
    private String name;

    @NotBlank(message = "Biển số xe không được để trống")
    private String licensePlate;

    @NotBlank(message = "Hãng xe không được để trống")
    private String make;

    @NotBlank(message = "Dòng xe không được để trống")
    private String model;

    @NotNull(message = "Năm sản xuất không được để trống")
    private Integer year;

    @NotNull(message = "ID nhóm không được để trống")
    @Positive(message = "ID nhóm phải là số dương")
    private Long groupId;

    @NotNull(message = "Giá mua không được để trống")
    @Positive(message = "Giá mua phải lớn hơn 0")
    private BigDecimal purchasePrice;

    private LocalDate purchaseDate;

    @Positive(message = "Dung lượng pin phải lớn hơn 0")
    private Double batteryCapacity;

    @Positive(message = "Số km ban đầu phải lớn hơn hoặc bằng 0")
    private Long initialOdometer;

    // Voting configuration
    @Future(message = "Thời gian kết thúc vote phải trong tương lai")
    private LocalDateTime voteEndTime;

    @Positive(message = "Tỷ lệ phê duyệt phải lớn hơn 0")
    private BigDecimal requiredApprovalPercentage;

    private String proposalDescription; // Mô tả proposal

    private String dealerInfo; // Thông tin đại lý/người bán

    private String attachmentUrls; // URLs hình ảnh/tài liệu

    private Boolean allowMemberContribution; // Cho phép thành viên đóng góp
}
