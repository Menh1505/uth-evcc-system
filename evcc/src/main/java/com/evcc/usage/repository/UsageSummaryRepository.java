package com.evcc.usage.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.evcc.usage.entity.UsageSummary;

@Repository
public interface UsageSummaryRepository extends JpaRepository<UsageSummary, Long> {

    /**
     * Tìm thống kê sử dụng theo contract và user, sắp xếp theo ngày giảm dần
     */
    List<UsageSummary> findByContractIdAndUserIdOrderByPeriodDateDesc(Long contractId, UUID userId);

    /**
     * Tìm thống kê sử dụng theo contract, user và khoảng thời gian
     */
    @Query("SELECT us FROM UsageSummary us WHERE us.contract.id = :contractId "
            + "AND us.user.id = :userId "
            + "AND us.periodDate BETWEEN :fromDate AND :toDate "
            + "ORDER BY us.periodDate DESC")
    List<UsageSummary> findByContractIdAndUserIdAndDateRange(@Param("contractId") Long contractId,
            @Param("userId") UUID userId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);

    /**
     * Tìm thống kê sử dụng theo contract và khoảng thời gian
     */
    @Query("SELECT us FROM UsageSummary us WHERE us.contract.id = :contractId "
            + "AND us.periodDate BETWEEN :fromDate AND :toDate "
            + "ORDER BY us.periodDate DESC")
    List<UsageSummary> findByContractIdAndDateRange(@Param("contractId") Long contractId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);

    /**
     * Tìm thống kê sử dụng theo vehicle và khoảng thời gian
     */
    @Query("SELECT us FROM UsageSummary us WHERE us.vehicle.id = :vehicleId "
            + "AND us.periodDate BETWEEN :fromDate AND :toDate "
            + "ORDER BY us.periodDate DESC")
    List<UsageSummary> findByVehicleIdAndDateRange(@Param("vehicleId") Long vehicleId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);

    /**
     * Tìm thống kê sử dụng theo user và khoảng thời gian
     */
    @Query("SELECT us FROM UsageSummary us WHERE us.user.id = :userId "
            + "AND us.periodDate BETWEEN :fromDate AND :toDate "
            + "ORDER BY us.periodDate DESC")
    List<UsageSummary> findByUserIdAndDateRange(@Param("userId") UUID userId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);

    /**
     * Tìm thống kê gần nhất theo contract và user
     */
    @Query("SELECT us FROM UsageSummary us WHERE us.contract.id = :contractId "
            + "AND us.user.id = :userId "
            + "ORDER BY us.periodDate DESC")
    List<UsageSummary> findLatestByContractIdAndUserId(@Param("contractId") Long contractId,
            @Param("userId") UUID userId);

    /**
     * Tìm những heavy user trong contract
     */
    @Query("SELECT us FROM UsageSummary us WHERE us.contract.id = :contractId "
            + "AND us.isHeavyUser = true "
            + "AND us.periodDate BETWEEN :fromDate AND :toDate "
            + "ORDER BY us.totalTrips DESC")
    List<UsageSummary> findHeavyUsersByContract(@Param("contractId") Long contractId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);

    /**
     * Tìm những reliable user trong contract
     */
    @Query("SELECT us FROM UsageSummary us WHERE us.contract.id = :contractId "
            + "AND us.isReliableUser = true "
            + "AND us.periodDate BETWEEN :fromDate AND :toDate "
            + "ORDER BY us.priorityScoreForNextPeriod DESC")
    List<UsageSummary> findReliableUsersByContract(@Param("contractId") Long contractId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);

    /**
     * Tính tổng số trips của tất cả user trong contract trong khoảng thời gian
     */
    @Query("SELECT COALESCE(SUM(us.totalTrips), 0) FROM UsageSummary us "
            + "WHERE us.contract.id = :contractId "
            + "AND us.periodDate BETWEEN :fromDate AND :toDate")
    Long sumTripsByContractAndDateRange(@Param("contractId") Long contractId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);

    /**
     * Tính tổng quãng đường của tất cả user trong contract trong khoảng thời
     * gian
     */
    @Query("SELECT COALESCE(SUM(us.totalDistance), 0) FROM UsageSummary us "
            + "WHERE us.contract.id = :contractId "
            + "AND us.periodDate BETWEEN :fromDate AND :toDate")
    Long sumDistanceByContractAndDateRange(@Param("contractId") Long contractId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);

    /**
     * Tìm top user sử dụng nhiều nhất trong contract
     */
    @Query("SELECT us FROM UsageSummary us WHERE us.contract.id = :contractId "
            + "AND us.periodDate BETWEEN :fromDate AND :toDate "
            + "ORDER BY us.totalTrips DESC, us.totalDistance DESC")
    List<UsageSummary> findTopUsersByUsage(@Param("contractId") Long contractId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);

    /**
     * Tìm user có điểm đánh giá cao nhất
     */
    @Query("SELECT us FROM UsageSummary us WHERE us.contract.id = :contractId "
            + "AND us.periodDate BETWEEN :fromDate AND :toDate "
            + "AND us.averageRating IS NOT NULL "
            + "ORDER BY us.averageRating DESC, us.totalTrips DESC")
    List<UsageSummary> findTopRatedUsers(@Param("contractId") Long contractId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);

    /**
     * Tìm thống kê theo loại period
     */
    @Query("SELECT us FROM UsageSummary us WHERE us.contract.id = :contractId "
            + "AND us.periodType = :periodType "
            + "AND us.periodDate BETWEEN :fromDate AND :toDate "
            + "ORDER BY us.periodDate DESC")
    List<UsageSummary> findByContractAndPeriodType(@Param("contractId") Long contractId,
            @Param("periodType") String periodType,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);

    /**
     * Kiểm tra xem có thống kê cho user/contract/period cụ thể không
     */
    @Query("SELECT COUNT(us) > 0 FROM UsageSummary us WHERE us.contract.id = :contractId "
            + "AND us.user.id = :userId "
            + "AND us.vehicle.id = :vehicleId "
            + "AND us.periodDate = :periodDate "
            + "AND us.periodType = :periodType")
    boolean existsByContractAndUserAndVehicleAndPeriod(@Param("contractId") Long contractId,
            @Param("userId") UUID userId,
            @Param("vehicleId") Long vehicleId,
            @Param("periodDate") LocalDate periodDate,
            @Param("periodType") String periodType);
}
         
         
         
         
         
         
         
         
         
         
         
         
         
         
