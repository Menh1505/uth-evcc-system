package com.evcc.booking.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO chứa thông tin ưu tiên booking của một thành viên
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingPriorityInfo {

    private UUID userId;
    private String username;
    private BigDecimal ownershipPercentage;
    private int priorityScore;
    private int ownershipScore;
    private int usageHistoryScore;
    private int reliabilityScore;
    private int finalPriorityScore;
    private String priorityLevel; // HIGH, MEDIUM, LOW
    private LocalDateTime lastUsed;
    private UsageStats recentUsage;
    private boolean canBook;
    private String remarks;

    /**
     * Nested class cho thống kê sử dụng
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UsageStats {
        private int totalTripsLast30Days;
        private int totalHoursLast30Days;
        private int totalDistanceLast30Days;
        private BigDecimal averageRating;
        private int lateCount;
        private int overtimeCount;
        private int cancellationCount;
        private int noShowCount;
        private BigDecimal onTimePercentage;
        private BigDecimal completionRate;
        private boolean isHeavyUser;
        private boolean isReliableUser;
        private BigDecimal usageAdjustmentFactor; // 0.5 - 1.5
    }

    /**
     * Tính mức độ ưu tiên dựa trên điểm số
     */
    public String calculatePriorityLevel() {
        if (finalPriorityScore >= 80) {
            return "HIGH";
        } else if (finalPriorityScore >= 60) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }

    /**
     * Kiểm tra xem có phải high priority user không
     */
    public boolean isHighPriority() {
        return "HIGH".equals(priorityLevel) || finalPriorityScore >= 80;
    }

    /**
     * Tính tỉ lệ sử dụng so với quyền sở hữu
     */
    public BigDecimal getUsageToOwnershipRatio() {
        if (ownershipPercentage.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        // Tính dựa trên usage trong 30 ngày gần đây
        if (recentUsage != null && recentUsage.totalHoursLast30Days > 0) {
            BigDecimal usagePercent = new BigDecimal(recentUsage.totalHoursLast30Days)
                    .divide(new BigDecimal(720), 4, java.math.RoundingMode.HALF_UP) // 720 = 30 days * 24 hours
                    .multiply(new BigDecimal(100));
                    
            return usagePercent.divide(ownershipPercentage, 4, java.math.RoundingMode.HALF_UP);
        }
        
        return BigDecimal.ONE; // Neutral nếu chưa có data
    }
}