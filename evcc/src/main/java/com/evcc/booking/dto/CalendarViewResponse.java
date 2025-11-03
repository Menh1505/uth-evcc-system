package com.evcc.booking.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho Calendar View hiển thị lịch sử dụng xe
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarViewResponse {

    private Long vehicleId;
    private String vehicleName;
    private String licensePlate;
    private LocalDate viewDate;
    private List<CalendarEvent> events;
    private CalendarSummary summary;

    /**
     * Event trong calendar (booking, maintenance...)
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CalendarEvent {
        private Long id;
        private String type; // BOOKING, MAINTENANCE, BLOCKED
        private String title;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String status; // CONFIRMED, PENDING, COMPLETED, CANCELLED
        private UserInfo user;
        private String color; // Màu hiển thị trên calendar
        private boolean allDay;
        private String notes;
        private boolean editable;
        private boolean recurring;
    }

    /**
     * Thông tin user trong event
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserInfo {
        private UUID id;
        private String username;
        private String displayName;
        private int priorityScore;
    }

    /**
     * Tóm tắt thống kê trong ngày
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CalendarSummary {
        private int totalEvents;
        private int confirmedBookings;
        private int pendingBookings;
        private int completedTrips;
        private int availableHours;
        private int bookedHours;
        private double utilizationRate; // Tỉ lệ sử dụng (%)
        private LocalDateTime nextAvailableTime;
        private boolean hasConflicts;
    }
}