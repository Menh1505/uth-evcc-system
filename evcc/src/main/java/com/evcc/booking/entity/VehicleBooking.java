package com.evcc.booking.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

import com.evcc.booking.enums.BookingStatus;
import com.evcc.booking.enums.BookingType;
import com.evcc.contract.entity.Contract;
import com.evcc.user.entity.User;
import com.evcc.vehicle.entity.Vehicle;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
 * Entity đại diện cho việc đặt lịch sử dụng xe Mỗi booking tương ứng với một
 * lần sử dụng xe của một thành viên
 */
@Entity
@Table(name = "vehicle_bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Mã đặt lịch duy nhất
     */
    @Column(name = "booking_reference", unique = true, nullable = false, length = 50)
    private String bookingReference;

    /**
     * Hợp đồng liên quan (để kiểm tra quyền sở hữu)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    /**
     * Xe được đặt lịch
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    /**
     * Người đặt lịch
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Recurring booking gốc (nếu booking này được tạo từ recurring booking)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recurring_booking_id")
    private RecurringBooking recurringBooking;

    /**
     * Thời gian bắt đầu sử dụng xe
     */
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    /**
     * Thời gian kết thúc sử dụng xe
     */
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    /**
     * Loại đặt lịch (một lần hay định kỳ)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "booking_type", nullable = false)
    private BookingType bookingType;

    /**
     * Trạng thái đặt lịch
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    /**
     * Mục đích sử dụng xe
     */
    @Column(columnDefinition = "TEXT")
    private String purpose;

    /**
     * Địa điểm xuất phát (tùy chọn)
     */
    @Column(name = "pickup_location", length = 255)
    private String pickupLocation;

    /**
     * Địa điểm đến (tùy chọn)
     */
    @Column(name = "destination", length = 255)
    private String destination;

    /**
     * Số km dự kiến
     */
    @Column(name = "estimated_distance")
    private Integer estimatedDistance;

    /**
     * Điểm ưu tiên tại thời điểm đặt lịch (để audit)
     */
    @Column(name = "priority_score")
    private Integer priorityScore;

    /**
     * Ghi chú từ người đặt lịch
     */
    @Column(columnDefinition = "TEXT")
    private String notes;

    /**
     * Thời gian check-in thực tế (khi bắt đầu sử dụng xe)
     */
    @Column(name = "actual_start_time")
    private LocalDateTime actualStartTime;

    /**
     * Thời gian check-out thực tế (khi trả xe)
     */
    @Column(name = "actual_end_time")
    private LocalDateTime actualEndTime;

    /**
     * Số km thực tế đã đi
     */
    @Column(name = "actual_distance")
    private Integer actualDistance;

    /**
     * Chi phí ước tính cho chuyến đi này
     */
    @Column(name = "estimated_cost", precision = 19, scale = 2)
    private BigDecimal estimatedCost;

    /**
     * Chi phí thực tế
     */
    @Column(name = "actual_cost", precision = 19, scale = 2)
    private BigDecimal actualCost;

    /**
     * Đánh giá của người dùng (1-5)
     */
    @Column(name = "user_rating")
    private Integer userRating;

    /**
     * Feedback từ người dùng
     */
    @Column(name = "user_feedback", columnDefinition = "TEXT")
    private String userFeedback;

    /**
     * Lý do hủy (nếu có)
     */
    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;

    /**
     * Người hủy booking (có thể khác người đặt)
     */
    @Column(name = "cancelled_by")
    private UUID cancelledBy;

    /**
     * Thời gian hủy
     */
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        // Tự động tạo booking reference nếu chưa có
        if (this.bookingReference == null || this.bookingReference.isEmpty()) {
            this.bookingReference = generateBookingReference();
        }

        // Mặc định là PENDING nếu chưa có status
        if (this.status == null) {
            this.status = BookingStatus.PENDING;
        }

        // Mặc định là ONE_TIME nếu chưa có type
        if (this.bookingType == null) {
            this.bookingType = BookingType.ONE_TIME;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Tạo mã booking tự động theo format: BK-YYYYMMDD-XXXXX
     */
    private String generateBookingReference() {
        LocalDateTime now = LocalDateTime.now();
        String dateStr = String.format("%04d%02d%02d",
                now.getYear(), now.getMonthValue(), now.getDayOfMonth());
        Random random = new Random();
        int randomSuffix = random.nextInt(100000);
        return "BK-" + dateStr + "-" + String.format("%05d", randomSuffix);
    }

    /**
     * Tính thời lượng sử dụng xe (phút)
     */
    public long getDurationInMinutes() {
        if (actualStartTime != null && actualEndTime != null) {
            return java.time.Duration.between(actualStartTime, actualEndTime).toMinutes();
        } else {
            return java.time.Duration.between(startTime, endTime).toMinutes();
        }
    }

    /**
     * Kiểm tra xem có bị trễ không
     */
    public boolean isLate() {
        return actualStartTime != null && actualStartTime.isAfter(startTime.plusMinutes(15));
    }

    /**
     * Kiểm tra xem có trả xe muộn không
     */
    public boolean isOvertime() {
        return actualEndTime != null && actualEndTime.isAfter(endTime);
    }

    /**
     * Kiểm tra booking có đang active không
     */
    public boolean isActive() {
        return status == BookingStatus.IN_PROGRESS;
    }

    /**
     * Kiểm tra có thể hủy không
     */
    public boolean isCancellable() {
        return status == BookingStatus.PENDING || status == BookingStatus.CONFIRMED;
    }
}
