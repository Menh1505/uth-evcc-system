package com.evcc.booking.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO chứa kết quả chi tiết của việc tính toán ưu tiên
 * Bao gồm breakdown điểm từng thành phần để audit và debug
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriorityCalculationResult {

    private UUID userId;
    private String username;
    private Long contractId;
    private LocalDateTime calculatedAt;

    // Các thành phần điểm số
    private int ownershipBaseScore;          // Điểm cơ bản từ tỉ lệ sở hữu (0-40)
    private int usageHistoryScore;           // Điểm từ lịch sử sử dụng (0-25)  
    private int reliabilityScore;            // Điểm từ độ tin cậy (0-20)
    private int recentActivityScore;         // Điểm từ hoạt động gần đây (0-10)
    private int fairnessAdjustment;          // Điểm điều chỉnh công bằng (±5)

    // Điểm tổng và kết quả
    private int totalScore;                  // Tổng điểm (0-100)
    private int finalPriority;               // Điểm ưu tiên cuối cùng sau adjustment
    private String priorityLevel;            // HIGH, MEDIUM, LOW
    private boolean eligible;                // Có đủ điều kiện đặt lịch không

    // Chi tiết tính toán
    private Map<String, Object> calculationDetails;
    private String explanation;              // Giải thích cách tính
    private LocalDateTime nextRecalculationTime; // Khi nào cần tính lại

    // Thông tin bối cảnh
    private BigDecimal ownershipPercentage;
    private BigDecimal usageAdjustmentFactor;
    private BookingPriorityInfo.UsageStats recentUsageStats;

    /**
     * Tính tổng điểm từ các thành phần
     */
    public void calculateTotalScore() {
        this.totalScore = ownershipBaseScore + usageHistoryScore + reliabilityScore 
                         + recentActivityScore + fairnessAdjustment;
        
        // Đảm bảo trong khoảng 0-100
        this.totalScore = Math.max(0, Math.min(100, this.totalScore));
        this.finalPriority = this.totalScore;
    }

    /**
     * Xác định mức độ ưu tiên dựa trên điểm số
     */
    public void determinePriorityLevel() {
        if (finalPriority >= 80) {
            this.priorityLevel = "HIGH";
        } else if (finalPriority >= 60) {
            this.priorityLevel = "MEDIUM";  
        } else {
            this.priorityLevel = "LOW";
        }
    }

    /**
     * Tạo giải thích chi tiết về cách tính điểm
     */
    public void generateExplanation() {
        StringBuilder sb = new StringBuilder();
        sb.append("Điểm ưu tiên được tính như sau:\n");
        sb.append(String.format("- Tỉ lệ sở hữu (%s%%): %d điểm\n", 
            ownershipPercentage.toString(), ownershipBaseScore));
        sb.append(String.format("- Lịch sử sử dụng: %d điểm\n", usageHistoryScore));
        sb.append(String.format("- Độ tin cậy: %d điểm\n", reliabilityScore));
        sb.append(String.format("- Hoạt động gần đây: %d điểm\n", recentActivityScore));
        
        if (fairnessAdjustment != 0) {
            sb.append(String.format("- Điều chỉnh công bằng: %+d điểm\n", fairnessAdjustment));
        }
        
        sb.append(String.format("Tổng: %d điểm (%s)\n", finalPriority, priorityLevel));
        
        if (!eligible) {
            sb.append("Lưu ý: Không đủ điều kiện đặt lịch do chưa hoàn thành nghĩa vụ tài chính.");
        }
        
        this.explanation = sb.toString();
    }

    /**
     * Kiểm tra xem có phải high priority không
     */
    public boolean isHighPriority() {
        return "HIGH".equals(priorityLevel);
    }

    /**
     * Tính thời gian cần tính toán lại (dựa trên mức độ thay đổi)
     */
    public void setNextRecalculationTime() {
        LocalDateTime base = LocalDateTime.now();
        
        // High priority user: tính lại mỗi ngày
        if (isHighPriority()) {
            this.nextRecalculationTime = base.plusDays(1);
        }
        // Medium priority: tính lại mỗi 3 ngày  
        else if ("MEDIUM".equals(priorityLevel)) {
            this.nextRecalculationTime = base.plusDays(3);
        }
        // Low priority: tính lại mỗi tuần
        else {
            this.nextRecalculationTime = base.plusWeeks(1);
        }
    }
}