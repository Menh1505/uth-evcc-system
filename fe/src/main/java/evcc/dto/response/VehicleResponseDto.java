package evcc.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho response vehicle từ backend
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleResponseDto {

    private Long id;
    private String name;
    private String licensePlate;
    private String make;
    private String model;
    private Integer year;
    private Long groupId;
    private String groupName;
    private BigDecimal purchasePrice;
    private LocalDate purchaseDate;
    private Integer batteryCapacity;
    private Long initialOdometer;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
