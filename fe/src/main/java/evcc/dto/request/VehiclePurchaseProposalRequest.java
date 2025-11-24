package evcc.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.*;

/**
 * DTO cho frontend vehicle purchase proposal
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
    private LocalDateTime voteEndTime;

    @DecimalMin(value = "50.0", message = "Tỷ lệ phê duyệt tối thiểu 50%")
    @DecimalMax(value = "100.0", message = "Tỷ lệ phê duyệt tối đa 100%")
    private BigDecimal requiredApprovalPercentage;

    @Size(max = 1000, message = "Mô tả không được vượt quá 1000 ký tự")
    private String proposalDescription;

    @Size(max = 500, message = "Thông tin đại lý không được vượt quá 500 ký tự")
    private String dealerInfo;

    @Size(max = 1000, message = "Đường dẫn tài liệu không được vượt quá 1000 ký tự")
    private String attachmentUrls;

    @Builder.Default
    private Boolean allowMemberContribution = true;
}
