package com.evcc.booking.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.evcc.booking.entity.RecurringBooking;
import com.evcc.booking.enums.RecurrenceFrequency;

/**
 * Repository cho RecurringBooking entity
 */
@Repository
public interface RecurringBookingRepository extends JpaRepository<RecurringBooking, Long> {

    /**
     * Tìm recurring booking theo reference
     */
    Optional<RecurringBooking> findByRecurringReference(String recurringReference);

    /**
     * Tìm recurring booking theo user
     */
    List<RecurringBooking> findByUser_IdOrderByCreatedAtDesc(UUID userId);

    /**
     * Tìm recurring booking theo vehicle
     */
    List<RecurringBooking> findByVehicle_IdOrderByStartDateDesc(Long vehicleId);

    /**
     * Tìm recurring booking theo contract
     */
    List<RecurringBooking> findByContract_IdOrderByCreatedAtDesc(Long contractId);

    /**
     * Tìm recurring booking active
     */
    @Query("SELECT r FROM RecurringBooking r WHERE r.status = 'CONFIRMED' "
            + "AND r.autoCreate = true "
            + "AND (r.endDate IS NULL OR r.endDate >= CURRENT_DATE) "
            + "ORDER BY r.startDate ASC")
    List<RecurringBooking> findActiveRecurringBookings();

    /**
     * Tìm recurring booking cần tạo booking mới
     */
    @Query("SELECT r FROM RecurringBooking r WHERE r.status = 'CONFIRMED' "
            + "AND r.autoCreate = true "
            + "AND (r.endDate IS NULL OR r.endDate >= :targetDate) "
            + "AND r.startDate <= :targetDate")
    List<RecurringBooking> findBookingsToCreateForDate(@Param("targetDate") LocalDate targetDate);

    /**
     * Tìm recurring booking theo frequency
     */
    List<RecurringBooking> findByRecurrenceFrequencyOrderByCreatedAtDesc(RecurrenceFrequency frequency);

    /**
     * Kiểm tra conflict với recurring booking khác
     */
    @Query("SELECT COUNT(r) FROM RecurringBooking r WHERE r.vehicle.id = :vehicleId "
            + "AND r.status = 'CONFIRMED' "
            + "AND (:excludeId IS NULL OR r.id != :excludeId) "
            + "AND ((r.startDate BETWEEN :startDate AND :endDate) "
            + "OR (r.endDate BETWEEN :startDate AND :endDate) "
            + "OR (r.startDate <= :startDate AND (r.endDate IS NULL OR r.endDate >= :endDate))) "
            + "AND r.startTime < :endTime AND r.endTime > :startTime")
    long countConflictingRecurringBookings(@Param("vehicleId") Long vehicleId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("startTime") java.time.LocalTime startTime,
            @Param("endTime") java.time.LocalTime endTime,
            @Param("excludeId") Long excludeId);

    /**
     * Đếm số lần đã tạo booking
     */
    @Query("SELECT COUNT(b) FROM VehicleBooking b WHERE b.recurringBooking.id = :recurringId")
    long countCreatedBookings(@Param("recurringId") Long recurringId);
}
