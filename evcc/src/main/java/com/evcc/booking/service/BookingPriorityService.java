package com.evcc.booking.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.evcc.booking.dto.BookingPriorityInfo;
import com.evcc.booking.dto.PriorityCalculationResult;

/**
 * Service tính toán ưu tiên đặt lịch sử dụng xe
 * Dựa trên tỉ lệ sở hữu và lịch sử sử dụng
 */
public interface BookingPriorityService {

    /**
     * Tính điểm ưu tiên cho user trong một contract tại thời điểm hiện tại
     * 
     * @param contractId ID hợp đồng
     * @param userId ID người dùng
     * @return điểm ưu tiên từ 0-100
     */
    int calculateUserPriority(Long contractId, UUID userId);

    /**
     * Tính điểm ưu tiên cho user trong một contract tại thời điểm cụ thể
     * 
     * @param contractId ID hợp đồng
     * @param userId ID người dùng
     * @param atTime thời điểm tính toán
     * @return điểm ưu tiên từ 0-100
     */
    int calculateUserPriorityAtTime(Long contractId, UUID userId, LocalDateTime atTime);

    /**
     * Lấy danh sách ưu tiên của tất cả thành viên trong contract
     * 
     * @param contractId ID hợp đồng
     * @return danh sách thành viên được sắp xếp theo độ ưu tiên giảm dần
     */
    List<BookingPriorityInfo> getContractMembersPriorityList(Long contractId);

    /**
     * Tính điểm ưu tiên chi tiết với breakdown từng thành phần
     * 
     * @param contractId ID hợp đồng
     * @param userId ID người dùng
     * @return kết quả chi tiết với điểm từng phần
     */
    PriorityCalculationResult calculateDetailedPriority(Long contractId, UUID userId);

    /**
     * Kiểm tra xem user có quyền đặt lịch trong khoảng thời gian không
     * 
     * @param contractId ID hợp đồng
     * @param userId ID người dùng
     * @param startTime thời gian bắt đầu
     * @param endTime thời gian kết thúc
     * @return true nếu có quyền đặt
     */
    boolean canUserBookAtTime(Long contractId, UUID userId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * Tìm thời gian khả dụng tốt nhất cho user trong khoảng thời gian
     * 
     * @param contractId ID hợp đồng
     * @param userId ID người dùng
     * @param preferredStart thời gian mong muốn bắt đầu
     * @param duration thời lượng cần sử dụng (phút)
     * @param flexibilityHours số giờ linh hoạt trước/sau thời gian mong muốn
     * @return thời gian bắt đầu khả dụi tốt nhất, null nếu không có
     */
    LocalDateTime findBestAvailableTime(Long contractId, UUID userId, LocalDateTime preferredStart, 
                                       int duration, int flexibilityHours);

    /**
     * Cập nhật lại điểm ưu tiên sau khi hoàn thành một booking
     * 
     * @param bookingId ID booking vừa hoàn thành
     */
    void updatePriorityAfterBookingCompletion(Long bookingId);

    /**
     * Tính toán adjustment factor dựa trên usage pattern gần đây
     * 
     * @param userId ID người dùng
     * @param contractId ID hợp đồng  
     * @param days số ngày gần đây để xem xét
     * @return factor từ 0.5 đến 1.5 (1.0 là neutral)
     */
    BigDecimal calculateRecentUsageAdjustment(UUID userId, Long contractId, int days);

    /**
     * Lấy thống kê sử dụng của user để tính ưu tiên
     * 
     * @param userId ID người dùng
     * @param contractId ID hợp đồng
     * @param days số ngày gần đây
     * @return thống kê sử dụng
     */
    BookingPriorityInfo.UsageStats getUserUsageStats(UUID userId, Long contractId, int days);
}