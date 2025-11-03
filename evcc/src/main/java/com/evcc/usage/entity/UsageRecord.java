package com.evcc.usage.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.evcc.booking.entity.VehicleBooking;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity ghi lại lịch sử sử dụng xe của từng thành viên
 * Dùng để tính toán ưu tiên và phân bổ chi phí
 */
@Entity
@Table(name = "usage_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsageRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Booking liên quan (nếu có)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private VehicleBooking booking;

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
     * Thời gian bắt đầu sử dụng
     */
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    /**
     * Thời gian kết thúc sử dụng
     */
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    /**
     * Số km đầu khi bắt đầu
     */
    @Column(name = "start_odometer")
    private Long startOdometer;

    /**
     * Số km cuối khi kết thúc
     */
    @Column(name = "end_odometer")
    private Long endOdometer;

    /**
     * Số km đã đi trong chuyến này
     */
    @Column(name = "distance_traveled", nullable = false)
    private Integer distanceTraveled;

    /**
     * Thời lượng sử dụng (phút)
     */
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    /**
     * Mức pin lúc bắt đầu (%)
     */
    @Column(name = "start_battery_level")
    private Integer startBatteryLevel;

    /**
     * Mức pin lúc kết thúc (%)
     */
    @Column(name = "end_battery_level")
    private Integer endBatteryLevel;

    /**
     * Lượng điện tiêu thụ (kWh)
     */
    @Column(name = "energy_consumed", precision = 8, scale = 2)
    private BigDecimal energyConsumed;

    /**
     * Chi phí ước tính cho chuyến đi
     */
    @Column(name = "estimated_cost", precision = 19, scale = 2)
    private BigDecimal estimatedCost;

    /**
     * Địa điểm xuất phát
     */
    @Column(name = "pickup_location", length = 255)
    private String pickupLocation;

    /**
     * Địa điểm đến
     */
    @Column(name = "destination", length = 255)
    private String destination;

    /**
     * Mục đích sử dụng
     */
    @Column(length = 255)
    private String purpose;

    /**
     * Tỉ lệ sở hữu tại thời điểm sử dụng (để audit)
     */
    @Column(name = "ownership_percentage_at_time", precision = 5, scale = 2)
    private BigDecimal ownershipPercentageAtTime;

    /**
     * Điểm ưu tiên tại thời điểm sử dụng
     */
    @Column(name = "priority_score_at_time")
    private Integer priorityScoreAtTime;

    /**
     * Có phải sử dụng trong giờ cao điểm không
     */
    @Column(name = "is_peak_hour")
    @Builder.Default
    private Boolean isPeakHour = false;

    /**
     * Có bị trễ so với lịch đặt không
     */
    @Column(name = "was_late")
    @Builder.Default
    private Boolean wasLate = false;

    /**
     * Có trả xe muộn không
     */
    @Column(name = "was_overtime")
    @Builder.Default
    private Boolean wasOvertime = false;

    /**
     * Đánh giá trải nghiệm (1-5)
     */
    @Column(name = "user_rating")
    private Integer userRating;

    /**
     * Feedback từ người dùng
     */
    @Column(name = "user_feedback", columnDefinition = "TEXT")
    private String userFeedback;

    /**
     * Ghi chú về chuyến đi
     */
    @Column(columnDefinition = "TEXT")
    private String notes;

    /**
     * Có sự cố xảy ra không
     */
    @Column(name = "had_incident")
    @Builder.Default
    private Boolean hadIncident = false;

    /**
     * Mô tả sự cố (nếu có)
     */
    @Column(name = "incident_description", columnDefinition = "TEXT")
    private String incidentDescription;

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
     * Tính hiệu suất sử dụng pin (km/kWh)
     */
    public BigDecimal getEnergyEfficiency() {
        if (energyConsumed != null && energyConsumed.compareTo(BigDecimal.ZERO) > 0) {
            return new BigDecimal(distanceTraveled).divide(energyConsumed, 2, java.math.RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    /**
     * Tính tốc độ trung bình (km/h)
     */
    public BigDecimal getAverageSpeed() {
        if (durationMinutes > 0) {
            BigDecimal hours = new BigDecimal(durationMinutes).divide(new BigDecimal(60), 2, java.math.RoundingMode.HALF_UP);
            return new BigDecimal(distanceTraveled).divide(hours, 2, java.math.RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    /**
     * Kiểm tra chuyến đi có hiệu quả không (dựa trên thời gian và quãng đường)
     */
    public boolean isEfficientTrip() {
        // Logic: nếu đi được > 1km/phút thì coi là hiệu quả
        if (durationMinutes > 0) {
            double kmPerMinute = (double) distanceTraveled / durationMinutes;
            return kmPerMinute > 1.0;
        }
        return false;
    }

    /**
     * Tính điểm đánh giá tổng thể cho chuyến đi (0-100)
     */
    public int calculateTripScore() {
        int score = 50; // Điểm cơ bản
        
        // Cộng điểm nếu không trễ
        if (!wasLate) score += 10;
        
        // Cộng điểm nếu không trả xe muộn
        if (!wasOvertime) score += 10;
        
        // Cộng điểm nếu không có sự cố
        if (!hadIncident) score += 15;
        
        // Cộng điểm dựa trên đánh giá user
        if (userRating != null) {
            score += userRating * 3; // 3-15 điểm
        }
        
        return Math.min(score, 100);
    }
}