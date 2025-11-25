package com.evcc.booking.dto;

import java.time.LocalDateTime;

import com.evcc.booking.enums.BookingStatus;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingUpdateRequest {

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String purpose;

    @Positive(message = "Estimated distance must be positive")
    private Integer estimatedDistance;

    private String destination;

    private String notes;

    private BookingStatus status;

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
