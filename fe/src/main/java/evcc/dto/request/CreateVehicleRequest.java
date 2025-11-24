package evcc.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO để tạo mới vehicle từ frontend
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateVehicleRequest {

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

    @Positive(message = "Giá mua phải lớn hơn 0")
    private BigDecimal purchasePrice;

    private LocalDate purchaseDate;

    @Positive(message = "Dung lượng pin phải lớn hơn 0")
    private Integer batteryCapacity;

    @Positive(message = "Số km ban đầu phải lớn hơn 0")
    private Long initialOdometer;

    private String status = "AVAILABLE";
}
