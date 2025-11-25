package evcc.dto.booking;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class BookingCreateRequestDto {

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

    // Constructors
    public BookingCreateRequestDto() {
    }

    // Getters and Setters
    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public Integer getEstimatedDistance() {
        return estimatedDistance;
    }

    public void setEstimatedDistance(Integer estimatedDistance) {
        this.estimatedDistance = estimatedDistance;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Form-friendly getters for date/time components
    public String getStartDate() {
        return startTime != null ? startTime.toLocalDate().toString() : "";
    }

    public String getStartTimeString() {
        return startTime != null ? startTime.toLocalTime().toString() : "";
    }

    public String getEndDate() {
        return endTime != null ? endTime.toLocalDate().toString() : "";
    }

    public String getEndTimeString() {
        return endTime != null ? endTime.toLocalTime().toString() : "";
    }
}
