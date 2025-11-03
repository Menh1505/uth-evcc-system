package com.evcc.booking.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.evcc.booking.enums.BookingStatus;
import com.evcc.booking.enums.RecurrenceFrequency;
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
 * Entity đại diện cho việc đặt lịch định kỳ
 * Sẽ tạo ra nhiều VehicleBooking theo pattern định kỳ
 */
@Entity
@Table(name = "recurring_bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecurringBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Mã đặt lịch định kỳ duy nhất
     */
    @Column(name = "recurring_reference", unique = true, nullable = false, length = 50)
    private String recurringReference;

    /**
     * Tiêu đề cho series đặt lịch
     */
    @Column(nullable = false, length = 255)
    private String title;

    /**
     * Hợp đồng liên quan
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
     * Ngày bắt đầu của pattern định kỳ
     */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /**
     * Ngày kết thúc của pattern định kỳ (có thể null = vô hạn)
     */
    @Column(name = "end_date")
    private LocalDate endDate;

    /**
     * Giờ bắt đầu sử dụng xe (cố định cho tất cả bookings)
     */
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    /**
     * Giờ kết thúc sử dụng xe (cố định cho tất cả bookings)
     */
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    /**
     * Tần suất lặp lại
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "recurrence_frequency", nullable = false)
    private RecurrenceFrequency recurrenceFrequency;

    /**
     * Khoảng cách giữa các lần lặp lại
     * VD: frequency = WEEKLY, interval = 2 => cứ 2 tuần một lần
     */
    @Column(name = "recurrence_interval", nullable = false)
    @Builder.Default
    private Integer recurrenceInterval = 1;

    /**
     * Các ngày trong tuần (nếu frequency = WEEKLY)
     * Format: "1,3,5" => Thứ 2, Thứ 4, Thứ 6 (1=Monday, 7=Sunday)
     */
    @Column(name = "days_of_week", length = 20)
    private String daysOfWeek;

    /**
     * Ngày trong tháng (nếu frequency = MONTHLY)
     * VD: 15 => ngày 15 hàng tháng
     */
    @Column(name = "day_of_month")
    private Integer dayOfMonth;

    /**
     * Trạng thái của recurring booking
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BookingStatus status = BookingStatus.CONFIRMED;

    /**
     * Mục đích sử dụng xe
     */
    @Column(columnDefinition = "TEXT")
    private String purpose;

    /**
     * Địa điểm xuất phát mặc định
     */
    @Column(name = "default_pickup_location", length = 255)
    private String defaultPickupLocation;

    /**
     * Địa điểm đến mặc định
     */
    @Column(name = "default_destination", length = 255)
    private String defaultDestination;

    /**
     * Số km dự kiến cho mỗi chuyến
     */
    @Column(name = "estimated_distance")
    private Integer estimatedDistance;

    /**
     * Ghi chú
     */
    @Column(columnDefinition = "TEXT")
    private String notes;

    /**
     * Số lượng bookings tối đa được tạo (null = không giới hạn)
     */
    @Column(name = "max_occurrences")
    private Integer maxOccurrences;

    /**
     * Số lượng bookings đã được tạo
     */
    @Column(name = "created_count", nullable = false)
    @Builder.Default
    private Integer createdCount = 0;

    /**
     * Có tự động tạo booking mới không
     */
    @Column(name = "auto_create", nullable = false)
    @Builder.Default
    private Boolean autoCreate = true;

    /**
     * Số ngày trước khi tạo booking mới (để review trước)
     */
    @Column(name = "create_days_ahead", nullable = false)
    @Builder.Default
    private Integer createDaysAhead = 7;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        
        // Tự động tạo recurring reference nếu chưa có
        if (this.recurringReference == null || this.recurringReference.isEmpty()) {
            this.recurringReference = generateRecurringReference();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Tạo mã recurring booking tự động theo format: RC-YYYYMMDD-XXXXX
     */
    private String generateRecurringReference() {
        LocalDateTime now = LocalDateTime.now();
        String dateStr = String.format("%04d%02d%02d", 
            now.getYear(), now.getMonthValue(), now.getDayOfMonth());
        int randomSuffix = (int)(Math.random() * 100000);
        return "RC-" + dateStr + "-" + String.format("%05d", randomSuffix);
    }

    /**
     * Kiểm tra xem có đang active không
     */
    public boolean isActive() {
        return status == BookingStatus.CONFIRMED && 
               (endDate == null || !LocalDate.now().isAfter(endDate));
    }

    /**
     * Kiểm tra xem có đủ quota tạo booking mới không
     */
    public boolean canCreateMore() {
        return maxOccurrences == null || createdCount < maxOccurrences;
    }

    /**
     * Tăng số lượng booking đã tạo
     */
    public void incrementCreatedCount() {
        this.createdCount++;
    }
}