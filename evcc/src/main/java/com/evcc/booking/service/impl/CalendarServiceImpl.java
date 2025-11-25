package com.evcc.booking.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evcc.booking.dto.CalendarViewResponse;
import com.evcc.booking.entity.VehicleBooking;
import com.evcc.booking.repository.VehicleBookingRepository;
import com.evcc.booking.service.CalendarService;
import com.evcc.contract.repository.ContractOwnershipRepository;
import com.evcc.vehicle.entity.Vehicle;
import com.evcc.vehicle.repository.VehicleRepository;

import lombok.RequiredArgsConstructor;

/**
 * Implementation cho CalendarService
 */
@Service
@Transactional
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {

    private static final Logger logger = LoggerFactory.getLogger(CalendarServiceImpl.class);

    private final VehicleBookingRepository bookingRepository;
    private final VehicleRepository vehicleRepository;
    private final ContractOwnershipRepository ownershipRepository;

    @Override
    @Transactional(readOnly = true)
    public CalendarViewResponse getDayView(Long vehicleId, LocalDate date) {
        logger.debug("Getting day view for vehicle {} on date {}", vehicleId, date);

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + vehicleId));

        LocalDateTime startOfDay = date.atStartOfDay();
        List<VehicleBooking> bookings = bookingRepository
                .findBookingsForVehicleOnDate(vehicleId, startOfDay);

        List<CalendarViewResponse.CalendarEvent> events = bookings.stream()
                .map(this::convertToCalendarEvent)
                .collect(Collectors.toList());

        CalendarViewResponse.CalendarSummary summary = calculateDaySummary(bookings, date);

        return CalendarViewResponse.builder()
                .vehicleId(vehicleId)
                .vehicleName(vehicle.getName())
                .licensePlate(vehicle.getLicensePlate())
                .viewDate(date)
                .events(events)
                .summary(summary)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CalendarViewResponse> getWeekView(Long vehicleId, LocalDate weekStartDate) {
        logger.debug("Getting week view for vehicle {} starting {}", vehicleId, weekStartDate);

        List<CalendarViewResponse> weekView = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            LocalDate currentDate = weekStartDate.plusDays(i);
            weekView.add(getDayView(vehicleId, currentDate));
        }

        return weekView;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CalendarViewResponse> getMonthView(Long vehicleId, int year, int month) {
        logger.debug("Getting month view for vehicle {} for {}/{}", vehicleId, month, year);

        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);

        List<CalendarViewResponse> monthView = new ArrayList<>();
        LocalDate currentDate = startOfMonth;

        while (!currentDate.isAfter(endOfMonth)) {
            monthView.add(getDayView(vehicleId, currentDate));
            currentDate = currentDate.plusDays(1);
        }

        return monthView;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocalDateTime> findAvailableSlots(Long vehicleId, LocalDate startDate,
            LocalDate endDate, int durationMinutes) {
        logger.debug("Finding available slots for vehicle {} from {} to {} for {} minutes",
                vehicleId, startDate, endDate, durationMinutes);

        List<LocalDateTime> availableSlots = new ArrayList<>();
        LocalDateTime searchStart = startDate.atTime(6, 0); // Start from 6 AM
        LocalDateTime searchEnd = endDate.atTime(22, 0); // End at 10 PM

        while (searchStart.isBefore(searchEnd)) {
            LocalDateTime slotEnd = searchStart.plusMinutes(durationMinutes);

            // Check if this slot conflicts with any booking
            long conflicts = bookingRepository.countConflictingBookings(
                    vehicleId, searchStart, slotEnd, null);

            if (conflicts == 0) {
                availableSlots.add(searchStart);
            }

            // Move to next hour
            searchStart = searchStart.plusHours(1);
        }

        return availableSlots;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasBookingConflict(Long vehicleId, LocalDateTime startTime,
            LocalDateTime endTime, Long excludeBookingId) {
        long conflicts = bookingRepository.countConflictingBookings(
                vehicleId, startTime, endTime, excludeBookingId);

        boolean hasConflict = conflicts > 0;
        logger.debug("Conflict check for vehicle {} from {} to {}: {}",
                vehicleId, startTime, endTime, hasConflict ? "CONFLICT" : "NO CONFLICT");

        return hasConflict;
    }

    @Override
    @Transactional(readOnly = true)
    public double getVehicleUtilizationRate(Long vehicleId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        Long totalUsageMinutes = bookingRepository.getTotalUsageMinutesForVehicle(
                vehicleId, startDateTime, endDateTime);

        if (totalUsageMinutes == null) {
            totalUsageMinutes = 0L;
        }

        long totalAvailableMinutes = ChronoUnit.MINUTES.between(startDateTime, endDateTime);

        double utilization = totalAvailableMinutes > 0
                ? (double) totalUsageMinutes / totalAvailableMinutes : 0.0;

        logger.debug("Utilization for vehicle {} from {} to {}: {:.2f}%",
                vehicleId, startDate, endDate, utilization * 100);

        return utilization;
    }

    @Override
    @Transactional(readOnly = true)
    public LocalDateTime getNextAvailableTime(Long vehicleId, LocalDateTime fromTime, int minDurationMinutes) {
        LocalDateTime searchTime = fromTime;
        LocalDateTime maxSearchTime = fromTime.plusDays(30); // Search for 30 days max

        while (searchTime.isBefore(maxSearchTime)) {
            LocalDateTime slotEnd = searchTime.plusMinutes(minDurationMinutes);

            if (!hasBookingConflict(vehicleId, searchTime, slotEnd, null)) {
                logger.debug("Next available time for vehicle {}: {}", vehicleId, searchTime);
                return searchTime;
            }

            // Move to next hour
            searchTime = searchTime.plusHours(1);
        }

        logger.warn("No available time found for vehicle {} after {}", vehicleId, fromTime);
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public String exportToICalendar(Long vehicleId, LocalDate startDate, LocalDate endDate) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + vehicleId));

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<VehicleBooking> bookings = bookingRepository.findBookingsInTimeRange(
                vehicleId, startDateTime, endDateTime);

        StringBuilder ical = new StringBuilder();
        ical.append("BEGIN:VCALENDAR\n");
        ical.append("VERSION:2.0\n");
        ical.append("PRODID:-//EVCC System//Vehicle Calendar//EN\n");
        ical.append("CALSCALE:GREGORIAN\n");

        for (VehicleBooking booking : bookings) {
            ical.append("BEGIN:VEVENT\n");
            ical.append("UID:").append(booking.getBookingReference()).append("\n");
            ical.append("SUMMARY:").append(vehicle.getName()).append(" - ")
                    .append(booking.getPurpose() != null ? booking.getPurpose() : "Vehicle Usage").append("\n");
            ical.append("DTSTART:").append(formatDateTimeForICal(booking.getStartTime())).append("\n");
            ical.append("DTEND:").append(formatDateTimeForICal(booking.getEndTime())).append("\n");
            ical.append("STATUS:").append(booking.getStatus().toString()).append("\n");
            ical.append("END:VEVENT\n");
        }

        ical.append("END:VCALENDAR\n");

        logger.info("Exported {} bookings to iCal for vehicle {}", bookings.size(), vehicleId);
        return ical.toString();
    }

    private CalendarViewResponse.CalendarEvent convertToCalendarEvent(VehicleBooking booking) {
        String eventColor = getEventColor(booking);

        return CalendarViewResponse.CalendarEvent.builder()
                .id(booking.getId())
                .type("BOOKING")
                .title(booking.getPurpose() != null ? booking.getPurpose() : "Vehicle Usage")
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .status(booking.getStatus().toString())
                .user(convertToUserInfo(booking))
                .color(eventColor)
                .allDay(false)
                .notes(booking.getNotes())
                .editable(booking.isCancellable())
                .recurring(booking.getBookingType().toString().equals("RECURRING"))
                .build();
    }

    private CalendarViewResponse.UserInfo convertToUserInfo(VehicleBooking booking) {
        return CalendarViewResponse.UserInfo.builder()
                .id(booking.getUser().getId())
                .username(booking.getUser().getUsername())
                .displayName(booking.getUser().getUsername())
                .priorityScore(booking.getPriorityScore() != null ? booking.getPriorityScore() : 0)
                .build();
    }

    private String getEventColor(VehicleBooking booking) {
        switch (booking.getStatus()) {
            case CONFIRMED:
                return "#4CAF50"; // Green
            case IN_PROGRESS:
                return "#FF9800"; // Orange
            case PENDING:
                return "#2196F3"; // Blue
            case COMPLETED:
                return "#9C27B0"; // Purple
            case CANCELLED:
                return "#F44336"; // Red
            default:
                return "#757575"; // Gray
        }
    }

    private CalendarViewResponse.CalendarSummary calculateDaySummary(List<VehicleBooking> bookings, LocalDate date) {
        int totalEvents = bookings.size();
        int confirmedBookings = (int) bookings.stream()
                .filter(b -> "CONFIRMED".equals(b.getStatus().toString()))
                .count();
        int pendingBookings = (int) bookings.stream()
                .filter(b -> "PENDING".equals(b.getStatus().toString()))
                .count();
        int completedTrips = (int) bookings.stream()
                .filter(b -> "COMPLETED".equals(b.getStatus().toString()))
                .count();

        // Calculate booked hours and available hours
        int bookedHours = bookings.stream()
                .mapToInt(b -> (int) ChronoUnit.HOURS.between(b.getStartTime(), b.getEndTime()))
                .sum();
        int availableHours = Math.max(0, 24 - bookedHours);

        double utilizationRate = bookedHours > 0 ? (double) bookedHours / 24 * 100 : 0.0;

        // Find next available time (simplified)
        LocalDateTime nextAvailableTime = date.atTime(6, 0); // Default to 6 AM next day
        boolean hasConflicts = bookings.stream()
                .anyMatch(b -> hasTimeOverlap(bookings, b));

        return CalendarViewResponse.CalendarSummary.builder()
                .totalEvents(totalEvents)
                .confirmedBookings(confirmedBookings)
                .pendingBookings(pendingBookings)
                .completedTrips(completedTrips)
                .availableHours(availableHours)
                .bookedHours(bookedHours)
                .utilizationRate(utilizationRate)
                .nextAvailableTime(nextAvailableTime)
                .hasConflicts(hasConflicts)
                .build();
    }

    private boolean hasTimeOverlap(List<VehicleBooking> bookings, VehicleBooking currentBooking) {
        return bookings.stream()
                .filter(b -> !b.getId().equals(currentBooking.getId()))
                .anyMatch(b -> timeRangesOverlap(
                currentBooking.getStartTime(), currentBooking.getEndTime(),
                b.getStartTime(), b.getEndTime()));
    }

    private boolean timeRangesOverlap(LocalDateTime start1, LocalDateTime end1,
            LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    private String formatDateTimeForICal(LocalDateTime dateTime) {
        return dateTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss"));
    }
}
