package com.evcc.usage.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.evcc.contract.entity.Contract;
import com.evcc.user.entity.User;
import com.evcc.vehicle.entity.Vehicle;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity tổng hợp thống kê sử dụng xe theo thời gian (hàng ngày/tuần/tháng)
 * Dùng để tính toán ưu tiên và phân tích sử dụng
 */
@Entity
@Table(name = "usage_summaries", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"contract_id", "user_id", "vehicle_id", "period_date", "period_type"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsageSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Hợp đồng liên quan
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    /**
     * Xe được sử dụng
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    /**
     * Người sử dụng
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Ngày của kỳ thống kê
     */
    @Column(name = "period_date", nullable = false)
    private LocalDate periodDate;

    /**
     * Loại kỳ thống kê: DAILY, WEEKLY, MONTHLY
     */
    @Column(name = "period_type", nullable = false, length = 10)
    private String periodType;

    /**
     * Tổng số lần sử dụng trong kỳ
     */
    @Column(name = "total_trips", nullable = false)
    @Builder.Default
    private Integer totalTrips = 0;

    /**
     * Tổng số km đã đi
     */
    @Column(name = "total_distance", nullable = false)
    @Builder.Default
    private Integer totalDistance = 0;

    /**
     * Tổng thời gian sử dụng (phút)
     */
    @Column(name = "total_duration_minutes", nullable = false)
    @Builder.Default
    private Integer totalDurationMinutes = 0;

    /**
     * Tổng lượng điện tiêu thụ (kWh)
     */
    @Column(name = "total_energy_consumed", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalEnergyConsumed = BigDecimal.ZERO;

    /**
     * Tổng chi phí ước tính
     */
    @Column(name = "total_estimated_cost", precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal totalEstimatedCost = BigDecimal.ZERO;

    /**
     * Số lần trễ
     */
    @Column(name = "late_count", nullable = false)
    @Builder.Default
    private Integer lateCount = 0;

    /**
     * Số lần trả xe muộn
     */
    @Column(name = "overtime_count", nullable = false)
    @Builder.Default
    private Integer overtimeCount = 0;

    /**
     * Số lần có sự cố
     */
    @Column(name = "incident_count", nullable = false)
    @Builder.Default
    private Integer incidentCount = 0;

    /**
     * Số lần hủy booking
     */
    @Column(name = "cancellation_count", nullable = false)
    @Builder.Default
    private Integer cancellationCount = 0;

    /**
     * Số lần no-show
     */
    @Column(name = "no_show_count", nullable = false)
    @Builder.Default
    private Integer noShowCount = 0;

    /**
     * Đánh giá trung bình
     */
    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating;

    /**
     * Điểm hiệu suất trung bình
     */
    @Column(name = "average_trip_score", precision = 5, scale = 2)
    private BigDecimal averageTripScore;

    /**
     * Hiệu suất sử dụng pin trung bình (km/kWh)
     */
    @Column(name = "average_energy_efficiency", precision = 6, scale = 2)
    private BigDecimal averageEnergyEfficiency;

    /**
     * Tỉ lệ sở hữu trung bình trong kỳ
     */
    @Column(name = "average_ownership_percentage", precision = 5, scale = 2)
    private BigDecimal averageOwnershipPercentage;

    /**
     * Điểm ưu tiên tính toán cho kỳ tiếp theo
     */
    @Column(name = "priority_score_for_next_period")
    private Integer priorityScoreForNextPeriod;

    /**
     * Có phải heavy user không (sử dụng > mức trung bình)
     */
    @Column(name = "is_heavy_user")
    @Builder.Default
    private Boolean isHeavyUser = false;

    /**
     * Có phải reliable user không (ít trễ, ít hủy)
     */
    @Column(name = "is_reliable_user")
    @Builder.Default
    private Boolean isReliableUser = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Tính tỉ lệ đúng giờ (%)
     */
    public BigDecimal getOnTimePercentage() {
        if (totalTrips == 0) return BigDecimal.ZERO;
        
        int onTimeTrips = totalTrips - lateCount;
        return new BigDecimal(onTimeTrips)
                .multiply(new BigDecimal(100))
                .divide(new BigDecimal(totalTrips), 2, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Tính tỉ lệ hoàn thành đúng hạn (%)
     */
    public BigDecimal getCompletionPercentage() {
        if (totalTrips == 0) return BigDecimal.ZERO;
        
        int completedTrips = totalTrips - overtimeCount;
        return new BigDecimal(completedTrips)
                .multiply(new BigDecimal(100))
                .divide(new BigDecimal(totalTrips), 2, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Tính tỉ lệ không có sự cố (%)
     */
    public BigDecimal getIncidentFreePercentage() {
        if (totalTrips == 0) return BigDecimal.ZERO;
        
        int incidentFreeTrips = totalTrips - incidentCount;
        return new BigDecimal(incidentFreeTrips)
                .multiply(new BigDecimal(100))
                .divide(new BigDecimal(totalTrips), 2, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Tính quãng đường trung bình mỗi chuyến
     */
    public BigDecimal getAverageDistancePerTrip() {
        if (totalTrips == 0) return BigDecimal.ZERO;
        
        return new BigDecimal(totalDistance)
                .divide(new BigDecimal(totalTrips), 2, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Tính thời gian trung bình mỗi chuyến (phút)
     */
    public BigDecimal getAverageDurationPerTrip() {
        if (totalTrips == 0) return BigDecimal.ZERO;
        
        return new BigDecimal(totalDurationMinutes)
                .divide(new BigDecimal(totalTrips), 2, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Tính điểm tổng thể cho user trong kỳ này (0-100)
     */
    public int calculateOverallScore() {
        if (totalTrips == 0) return 50; // Điểm neutral nếu chưa có data
        
        int score = 0;
        
        // 30% từ tỉ lệ đúng giờ
        score += getOnTimePercentage().multiply(new BigDecimal("0.3")).intValue();
        
        // 20% từ tỉ lệ hoàn thành đúng hạn  
        score += getCompletionPercentage().multiply(new BigDecimal("0.2")).intValue();
        
        // 20% từ tỉ lệ không sự cố
        score += getIncidentFreePercentage().multiply(new BigDecimal("0.2")).intValue();
        
        // 15% từ đánh giá trung bình
        if (averageRating != null) {
            score += averageRating.multiply(new BigDecimal(3)).intValue(); // 0-15 điểm
        }
        
        // 15% từ hiệu suất sử dụng
        if (averageTripScore != null) {
            score += averageTripScore.multiply(new BigDecimal("0.15")).intValue();
        }
        
        return Math.min(score, 100);
    }
}