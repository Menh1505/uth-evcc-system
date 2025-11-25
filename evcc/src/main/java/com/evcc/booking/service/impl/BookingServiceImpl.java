package com.evcc.booking.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evcc.booking.dto.BookingCreateRequest;
import com.evcc.booking.dto.BookingResponse;
import com.evcc.booking.dto.BookingSearchCriteria;
import com.evcc.booking.dto.BookingUpdateRequest;
import com.evcc.booking.entity.VehicleBooking;
import com.evcc.booking.enums.BookingStatus;
import com.evcc.booking.repository.VehicleBookingRepository;
import com.evcc.booking.service.BookingPriorityService;
import com.evcc.booking.service.BookingService;
import com.evcc.contract.entity.Contract;
import com.evcc.contract.repository.ContractRepository;
import com.evcc.user.entity.User;
import com.evcc.user.repository.UserRepository;
import com.evcc.vehicle.entity.Vehicle;
import com.evcc.vehicle.repository.VehicleRepository;

/**
 * Implementation cho BookingService
 */
@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingServiceImpl.class);

    private final VehicleBookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ContractRepository contractRepository;
    private final VehicleRepository vehicleRepository;
    private final BookingPriorityService priorityService;

    public BookingServiceImpl(VehicleBookingRepository bookingRepository,
            UserRepository userRepository,
            ContractRepository contractRepository,
            VehicleRepository vehicleRepository,
            BookingPriorityService priorityService) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.contractRepository = contractRepository;
        this.vehicleRepository = vehicleRepository;
        this.priorityService = priorityService;
    }

    @Override
    public BookingResponse createBooking(BookingCreateRequest request) {
        logger.debug("Creating booking for user {} on vehicle {}",
                request.getUserId(), request.getVehicleId());

        // Validate user exists
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Validate vehicle exists
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

        // Validate contract exists and user has access
        Contract contract = contractRepository.findById(request.getContractId())
                .orElseThrow(() -> new IllegalArgumentException("Contract not found"));

        // Check if user can book at this time
        if (!priorityService.canUserBookAtTime(request.getContractId(), request.getUserId(),
                request.getStartTime(), request.getEndTime())) {
            throw new IllegalArgumentException("User cannot book at the requested time due to priority rules");
        }

        // Check for time conflicts
        List<VehicleBooking> conflictingBookings = bookingRepository
                .findBookingsInTimeRange(request.getVehicleId(),
                        request.getStartTime(), request.getEndTime());

        if (!conflictingBookings.isEmpty()) {
            throw new IllegalArgumentException("Vehicle is already booked during the requested time period");
        }

        // Calculate estimated cost
        BigDecimal estimatedCost = calculateEstimatedCost(request.getStartTime(), request.getEndTime(), request.getEstimatedDistance());        // Create booking entity
        VehicleBooking booking = VehicleBooking.builder()
                .user(user)
                .vehicle(vehicle)
                .contract(contract)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .purpose(request.getPurpose())
                .estimatedDistance(request.getEstimatedDistance())
                .destination(request.getDestination())
                .estimatedCost(estimatedCost)
                .status(BookingStatus.PENDING)
                .notes(request.getNotes())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        VehicleBooking savedBooking = bookingRepository.save(booking);
        logger.info("Created booking {} for user {}", savedBooking.getId(), user.getId());

        return convertToResponse(savedBooking);
    }

    @Override
    public BookingResponse updateBooking(Long bookingId, BookingUpdateRequest request) {
        logger.debug("Updating booking {}", bookingId);

        VehicleBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        // Check if booking can be updated (only pending or confirmed bookings)
        if (booking.getStatus() == BookingStatus.COMPLETED
                || booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalArgumentException("Cannot update completed or cancelled bookings");
        }

        // Update fields if provided
        if (request.getStartTime() != null && request.getEndTime() != null) {
            // Check for new conflicts if time is being changed
            List<VehicleBooking> conflictingBookings = bookingRepository
                    .findBookingsInTimeRange(booking.getVehicle().getId(),
                            request.getStartTime(), request.getEndTime())
                    .stream()
                    .filter(b -> !b.getId().equals(bookingId)) // Exclude current booking
                    .toList();

            if (!conflictingBookings.isEmpty()) {
                throw new IllegalArgumentException("Vehicle is already booked during the new requested time period");
            }

            booking.setStartTime(request.getStartTime());
            booking.setEndTime(request.getEndTime());
        }

        if (request.getPurpose() != null) {
            booking.setPurpose(request.getPurpose());
        }

        if (request.getEstimatedDistance() != null) {
            booking.setEstimatedDistance(request.getEstimatedDistance());
        }

        if (request.getDestination() != null) {
            booking.setDestination(request.getDestination());
        }

        if (request.getNotes() != null) {
            booking.setNotes(request.getNotes());
        }

        if (request.getStatus() != null) {
            booking.setStatus(request.getStatus());
        }

        // Recalculate cost if time or distance changed
        if (request.getStartTime() != null || request.getEstimatedDistance() != null) {
            BigDecimal newCost = calculateEstimatedCost(booking.getStartTime(), booking.getEndTime(), booking.getEstimatedDistance());
            booking.setEstimatedCost(newCost);
        }
        booking.setUpdatedAt(LocalDateTime.now());
        VehicleBooking updatedBooking = bookingRepository.save(booking);

        logger.info("Updated booking {}", updatedBooking.getId());
        return convertToResponse(updatedBooking);
    }

    @Override
    public void cancelBooking(Long bookingId, UUID userId, String reason) {
        logger.debug("Cancelling booking {} by user {}", bookingId, userId);

        VehicleBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        // Check if user owns the booking or has admin rights
        if (!booking.getUser().getId().equals(userId)) {
            // In a real system, you might check for admin permissions here
            throw new IllegalArgumentException("User can only cancel their own bookings");
        }

        // Check if booking can be cancelled
        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new IllegalArgumentException("Cannot cancel completed bookings");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalArgumentException("Booking is already cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancellationReason(reason);
        booking.setCancelledAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());

        bookingRepository.save(booking);
        logger.info("Cancelled booking {} by user {}", bookingId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long bookingId) {
        VehicleBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        return convertToResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getUserBookings(UUID userId, LocalDate fromDate, LocalDate toDate) {
        logger.debug("Getting bookings for user {} from {} to {}", userId, fromDate, toDate);

        List<VehicleBooking> bookings;
        if (fromDate != null && toDate != null) {
            // Use existing method with LocalDateTime conversion
            LocalDateTime startDateTime = fromDate.atStartOfDay();
            LocalDateTime endDateTime = toDate.plusDays(1).atStartOfDay();

            bookings = bookingRepository.findByUser_IdOrderByCreatedAtDesc(userId)
                    .stream()
                    .filter(b -> !b.getStartTime().isBefore(startDateTime)
                    && !b.getStartTime().isAfter(endDateTime))
                    .collect(Collectors.toList());
        } else {
            bookings = bookingRepository.findByUser_IdOrderByCreatedAtDesc(userId);
        }
        return bookings.stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getVehicleBookings(Long vehicleId, LocalDate fromDate, LocalDate toDate) {
        logger.debug("Getting bookings for vehicle {} from {} to {}", vehicleId, fromDate, toDate);

        List<VehicleBooking> bookings;
        if (fromDate != null && toDate != null) {
            LocalDateTime startDateTime = fromDate.atStartOfDay();
            LocalDateTime endDateTime = toDate.plusDays(1).atStartOfDay();

            bookings = bookingRepository.findByVehicle_IdOrderByStartTimeDesc(vehicleId)
                    .stream()
                    .filter(b -> !b.getStartTime().isBefore(startDateTime)
                    && !b.getStartTime().isAfter(endDateTime))
                    .collect(Collectors.toList());
        } else {
            bookings = bookingRepository.findByVehicle_IdOrderByStartTimeDesc(vehicleId);
        }

        return bookings.stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> searchBookings(BookingSearchCriteria criteria) {
        logger.debug("Searching bookings with criteria: {}", criteria);

        List<VehicleBooking> bookings = new ArrayList<>();

        // This is a simplified search - in a real system you might use JPA Criteria API or Specifications
        if (criteria.getUserId() != null) {
            List<VehicleBooking> userBookings = bookingRepository.findByUser_IdOrderByCreatedAtDesc(criteria.getUserId());
            if (criteria.getFromDate() != null && criteria.getToDate() != null) {
                LocalDateTime startDateTime = criteria.getFromDate().atStartOfDay();
                LocalDateTime endDateTime = criteria.getToDate().plusDays(1).atStartOfDay();
                bookings.addAll(userBookings.stream()
                        .filter(b -> !b.getStartTime().isBefore(startDateTime)
                        && !b.getStartTime().isAfter(endDateTime))
                        .collect(Collectors.toList()));
            } else {
                bookings.addAll(userBookings);
            }
        } else if (criteria.getVehicleId() != null) {
            List<VehicleBooking> vehicleBookings = bookingRepository.findByVehicle_IdOrderByStartTimeDesc(criteria.getVehicleId());
            if (criteria.getFromDate() != null && criteria.getToDate() != null) {
                LocalDateTime startDateTime = criteria.getFromDate().atStartOfDay();
                LocalDateTime endDateTime = criteria.getToDate().plusDays(1).atStartOfDay();
                bookings.addAll(vehicleBookings.stream()
                        .filter(b -> !b.getStartTime().isBefore(startDateTime)
                        && !b.getStartTime().isAfter(endDateTime))
                        .collect(Collectors.toList()));
            } else {
                bookings.addAll(vehicleBookings);
            }
        } else if (criteria.getContractId() != null) {
            bookings.addAll(bookingRepository.findByContract_IdOrderByCreatedAtDesc(criteria.getContractId()));
        }        // Filter by status if specified
        if (criteria.getStatus() != null) {
            bookings = bookings.stream()
                    .filter(booking -> booking.getStatus() == criteria.getStatus())
                    .toList();
        }

        // Apply limit if specified
        if (criteria.getLimit() != null && criteria.getLimit() > 0) {
            bookings = bookings.stream()
                    .limit(criteria.getLimit())
                    .toList();
        }

        return bookings.stream()
                .map(this::convertToResponse)
                .toList();
    }

    // Private helper methods
    private BigDecimal calculateEstimatedCost(LocalDateTime startTime,
            LocalDateTime endTime, Integer estimatedDistance) {
        // This is a simplified cost calculation
        // In a real system, this would be more complex based on vehicle type, distance, time, etc.

        long durationHours = java.time.Duration.between(startTime, endTime).toHours();

        // Base hourly rate (could be stored in vehicle or contract)
        BigDecimal hourlyRate = BigDecimal.valueOf(10.0); // $10/hour
        BigDecimal distanceRate = BigDecimal.valueOf(0.5); // $0.5/km

        BigDecimal timeCost = hourlyRate.multiply(BigDecimal.valueOf(durationHours));
        BigDecimal distanceCost = estimatedDistance != null
                ? distanceRate.multiply(BigDecimal.valueOf(estimatedDistance)) : BigDecimal.ZERO;

        return timeCost.add(distanceCost);
    }

    private BookingResponse convertToResponse(VehicleBooking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .userId(booking.getUser().getId())
                .userFullName(booking.getUser().getUsername()) // Using username instead of fullName
                .vehicleId(booking.getVehicle().getId())
                .vehiclePlateNumber("N/A") // Placeholder - field doesn't exist in Vehicle entity
                .contractId(booking.getContract().getId())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .purpose(booking.getPurpose())
                .estimatedDistance(booking.getEstimatedDistance())
                .destination(booking.getDestination())
                .estimatedCost(booking.getEstimatedCost())
                .actualCost(booking.getActualCost())
                .status(booking.getStatus())
                .notes(booking.getNotes())
                .cancellationReason(booking.getCancellationReason())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .cancelledAt(booking.getCancelledAt())
                .build();
    }
}
