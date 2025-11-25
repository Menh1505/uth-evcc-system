package com.evcc.booking.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.evcc.booking.enums.BookingStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {

    private Long id;
    private UUID userId;
    private String userFullName;
    private Long vehicleId;
    private String vehiclePlateNumber;
    private Long contractId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String purpose;
    private Integer estimatedDistance;
    private String destination;
    private BigDecimal estimatedCost;
    private BigDecimal actualCost;
    private BookingStatus status;
    private String notes;
    private String cancellationReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime cancelledAt;
}
