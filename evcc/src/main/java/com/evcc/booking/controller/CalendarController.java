package com.evcc.booking.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.evcc.booking.dto.CalendarViewResponse;
import com.evcc.booking.service.CalendarService;

import lombok.RequiredArgsConstructor;

/**
 * REST Controller for calendar operations
 */
@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    /**
     * Get day view for a vehicle
     */
    @GetMapping("/vehicle/{vehicleId}/day")
    public ResponseEntity<CalendarViewResponse> getDayView(
            @PathVariable Long vehicleId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            CalendarViewResponse dayView = calendarService.getDayView(vehicleId, date);
            return ResponseEntity.ok(dayView);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get week view for a vehicle
     */
    @GetMapping("/vehicle/{vehicleId}/week")
    public ResponseEntity<List<CalendarViewResponse>> getWeekView(
            @PathVariable Long vehicleId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart) {
        try {
            List<CalendarViewResponse> weekView = calendarService.getWeekView(vehicleId, weekStart);
            return ResponseEntity.ok(weekView);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get month view for a vehicle
     */
    @GetMapping("/vehicle/{vehicleId}/month")
    public ResponseEntity<List<CalendarViewResponse>> getMonthView(
            @PathVariable Long vehicleId,
            @RequestParam int year,
            @RequestParam int month) {
        try {
            List<CalendarViewResponse> monthView = calendarService.getMonthView(vehicleId, year, month);
            return ResponseEntity.ok(monthView);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Find available time slots
     */
    @GetMapping("/vehicle/{vehicleId}/available")
    public ResponseEntity<List<LocalDateTime>> findAvailableSlots(
            @PathVariable Long vehicleId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam int durationMinutes) {
        try {
            List<LocalDateTime> availableSlots = calendarService.findAvailableSlots(
                    vehicleId, startDate, endDate, durationMinutes);
            return ResponseEntity.ok(availableSlots);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Check for booking conflicts
     */
    @GetMapping("/vehicle/{vehicleId}/conflict-check")
    public ResponseEntity<Boolean> checkConflict(
            @PathVariable Long vehicleId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(required = false) Long excludeBookingId) {
        try {
            boolean hasConflict = calendarService.hasBookingConflict(vehicleId, startTime, endTime, excludeBookingId);
            return ResponseEntity.ok(hasConflict);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get vehicle utilization rate
     */
    @GetMapping("/vehicle/{vehicleId}/utilization")
    public ResponseEntity<Double> getUtilizationRate(
            @PathVariable Long vehicleId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            double utilizationRate = calendarService.getVehicleUtilizationRate(vehicleId, startDate, endDate);
            return ResponseEntity.ok(utilizationRate);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get next available time
     */
    @GetMapping("/vehicle/{vehicleId}/next-available")
    public ResponseEntity<LocalDateTime> getNextAvailableTime(
            @PathVariable Long vehicleId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromTime,
            @RequestParam int minDurationMinutes) {
        try {
            LocalDateTime nextAvailable = calendarService.getNextAvailableTime(vehicleId, fromTime, minDurationMinutes);
            return ResponseEntity.ok(nextAvailable);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Export calendar to iCal format
     */
    @GetMapping("/vehicle/{vehicleId}/export")
    public ResponseEntity<String> exportCalendar(
            @PathVariable Long vehicleId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            String icalContent = calendarService.exportToICalendar(vehicleId, startDate, endDate);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentDispositionFormData("attachment", "vehicle-" + vehicleId + "-calendar.ics");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(icalContent);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}







