package com.evcc.booking.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.evcc.booking.entity.VehicleBooking;
import com.evcc.booking.enums.BookingStatus;

/**
 * Repository cho VehicleBooking entity
 */
@Repository
public interface VehicleBookingRepository extends JpaRepository<VehicleBooking, Long> {

    /**
     * Tìm booking theo reference
     */
    Optional<VehicleBooking> findByBookingReference(String bookingReference);

    /**
     * Tìm booking theo user
     */
    List<VehicleBooking> findByUser_IdOrderByCreatedAtDesc(UUID userId);

    Page<VehicleBooking> findByUser_Id(UUID userId, Pageable pageable);

    /**
     * Tìm booking theo vehicle
     */
    List<VehicleBooking> findByVehicle_IdOrderByStartTimeDesc(Long vehicleId);

    /**
     * Tìm booking theo contract
     */
    List<VehicleBooking> findByContract_IdOrderByCreatedAtDesc(Long contractId);

    List<VehicleBooking> findByContractId(Long contractId);

    /**
     * Tìm booking theo user và contract trong khoảng thời gian
     */
    @Query("SELECT b FROM VehicleBooking b WHERE b.user.id = :userId "
            + "AND b.contract.id = :contractId "
            + "AND b.startTime >= :startTime")
    List<VehicleBooking> findByUserIdAndContractIdAndStartTimeAfter(@Param("userId") UUID userId,
            @Param("contractId") Long contractId,
            @Param("startTime") LocalDateTime startTime);

    /**
     * Tìm booking theo user và contract trong khoảng thời gian cụ thể
     */
    @Query("SELECT b FROM VehicleBooking b WHERE b.user.id = :userId "
            + "AND b.contract.id = :contractId "
            + "AND b.startTime >= :startTime "
            + "AND b.startTime <= :endTime")
    List<VehicleBooking> findByUserIdAndContractIdAndStartTimeBetween(@Param("userId") UUID userId,
            @Param("contractId") Long contractId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * Tìm booking conflict theo contract
     */
    @Query("SELECT b FROM VehicleBooking b WHERE b.contract.id = :contractId "
            + "AND ((b.startTime BETWEEN :startTime AND :endTime) "
            + "OR (b.endTime BETWEEN :startTime AND :endTime) "
            + "OR (b.startTime <= :startTime AND b.endTime >= :endTime))")
    List<VehicleBooking> findConflictingBookings(@Param("contractId") Long contractId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * Tìm booking theo status
     */
    List<VehicleBooking> findByStatusOrderByCreatedAtDesc(BookingStatus status);

    /**
     * Tìm booking trong khoảng thời gian
     */
    @Query("SELECT b FROM VehicleBooking b WHERE b.vehicle.id = :vehicleId "
            + "AND ((b.startTime BETWEEN :startTime AND :endTime) "
            + "OR (b.endTime BETWEEN :startTime AND :endTime) "
            + "OR (b.startTime <= :startTime AND b.endTime >= :endTime))")
    List<VehicleBooking> findBookingsInTimeRange(@Param("vehicleId") Long vehicleId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * Kiểm tra conflict booking (trừ booking hiện tại)
     */
    @Query("SELECT COUNT(b) FROM VehicleBooking b WHERE b.vehicle.id = :vehicleId "
            + "AND b.status IN ('CONFIRMED', 'IN_PROGRESS') "
            + "AND (:excludeId IS NULL OR b.id != :excludeId) "
            + "AND ((b.startTime BETWEEN :startTime AND :endTime) "
            + "OR (b.endTime BETWEEN :startTime AND :endTime) "
            + "OR (b.startTime <= :startTime AND b.endTime >= :endTime))")
    long countConflictingBookings(@Param("vehicleId") Long vehicleId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("excludeId") Long excludeId);

    /**
     * Lấy booking active cho vehicle
     */
    @Query("SELECT b FROM VehicleBooking b WHERE b.vehicle.id = :vehicleId "
            + "AND b.status = 'IN_PROGRESS' ORDER BY b.startTime DESC")
    Optional<VehicleBooking> findActiveBookingForVehicle(@Param("vehicleId") Long vehicleId);

    /**
     * Lấy booking trong ngày cho vehicle
     */
    @Query("SELECT b FROM VehicleBooking b WHERE b.vehicle.id = :vehicleId "
            + "AND DATE(b.startTime) = DATE(:date) "
            + "ORDER BY b.startTime ASC")
    List<VehicleBooking> findBookingsForVehicleOnDate(@Param("vehicleId") Long vehicleId,
            @Param("date") LocalDateTime date);

    /**
     * Lấy booking sắp tới của user
     */
    @Query("SELECT b FROM VehicleBooking b WHERE b.user.id = :userId "
            + "AND b.startTime > CURRENT_TIMESTAMP "
            + "AND b.status IN ('CONFIRMED', 'PENDING') "
            + "ORDER BY b.startTime ASC")
    List<VehicleBooking> findUpcomingBookingsForUser(@Param("userId") UUID userId);

    /**
     * Thống kê booking theo status
     */
    @Query("SELECT b.status, COUNT(b) FROM VehicleBooking b "
            + "WHERE b.vehicle.id = :vehicleId GROUP BY b.status")
    List<Object[]> getBookingStatsByVehicle(@Param("vehicleId") Long vehicleId);

    /**
     * Tính utilization rate cho vehicle trong khoảng thời gian
     */
    @Query("SELECT SUM(TIMESTAMPDIFF(MINUTE, b.actualStartTime, b.actualEndTime)) "
            + "FROM VehicleBooking b WHERE b.vehicle.id = :vehicleId "
            + "AND b.status = 'COMPLETED' "
            + "AND b.actualStartTime >= :startTime "
            + "AND b.actualEndTime <= :endTime")
    Long getTotalUsageMinutesForVehicle(@Param("vehicleId") Long vehicleId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}
