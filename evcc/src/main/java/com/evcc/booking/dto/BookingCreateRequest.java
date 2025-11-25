package com.evcc.booking.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingCreateRequest {

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotNull(message = "Vehicle ID is required")
    private Long vehicleId;

    @NotNull(message = "Contract ID is required")
    private Long contractId;

    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    @Future(message = "End time must be in the future")
    private LocalDateTime endTime;

    @NotNull(message = "Purpose is required")
    private String purpose;

    @Positive(message = "Estimated distance must be positive")
    private Integer estimatedDistance;

    private String destination;

    private String notes;

    // Constructor validation
    public void setEndTime(LocalDateTime endTime) {
        if (this.startTime != null && endTime != null && endTime.isBefore(this.startTime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }
        this.endTime = endTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        if (this.endTime != null && startTime != null && this.endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        this.startTime = startTime;
    }
}
