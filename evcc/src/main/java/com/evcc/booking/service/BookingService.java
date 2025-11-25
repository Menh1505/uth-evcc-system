package com.evcc.booking.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.evcc.booking.dto.BookingCreateRequest;
import com.evcc.booking.dto.BookingResponse;
import com.evcc.booking.dto.BookingSearchCriteria;
import com.evcc.booking.dto.BookingUpdateRequest;

/**
 * Service interface for vehicle booking operations
 */
public interface BookingService {

    /**
     * Create a new booking
     *
     * @param request booking details
     * @return created booking information
     * @throws IllegalArgumentException if request is invalid or conflicts exist
     */
    BookingResponse createBooking(BookingCreateRequest request);

    /**
     * Update an existing booking
     *
     * @param bookingId ID of the booking to update
     * @param request update details
     * @return updated booking information
     * @throws IllegalArgumentException if booking not found or cannot be
     * updated
     */
    BookingResponse updateBooking(Long bookingId, BookingUpdateRequest request);

    /**
     * Cancel a booking
     *
     * @param bookingId ID of the booking to cancel
     * @param userId ID of the user requesting cancellation
     * @param reason reason for cancellation
     * @throws IllegalArgumentException if booking not found or cannot be
     * cancelled
     */
    void cancelBooking(Long bookingId, UUID userId, String reason);

    /**
     * Get booking details by ID
     *
     * @param bookingId booking ID
     * @return booking information
     * @throws IllegalArgumentException if booking not found
     */
    BookingResponse getBookingById(Long bookingId);

    /**
     * Get all bookings for a user within a date range
     *
     * @param userId user ID
     * @param fromDate start date (optional)
     * @param toDate end date (optional)
     * @return list of user bookings
     */
    List<BookingResponse> getUserBookings(UUID userId, LocalDate fromDate, LocalDate toDate);

    /**
     * Get all bookings for a vehicle within a date range
     *
     * @param vehicleId vehicle ID
     * @param fromDate start date (optional)
     * @param toDate end date (optional)
     * @return list of vehicle bookings
     */
    List<BookingResponse> getVehicleBookings(Long vehicleId, LocalDate fromDate, LocalDate toDate);

    /**
     * Search bookings by various criteria
     *
     * @param criteria search criteria
     * @return list of matching bookings
     */
    List<BookingResponse> searchBookings(BookingSearchCriteria criteria);
}
