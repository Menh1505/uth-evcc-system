package com.evcc.booking.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.evcc.booking.dto.CalendarViewResponse;

/**
 * Service cho Calendar API - hiển thị lịch sử dụng xe
 */
public interface CalendarService {

    /**
     * Lấy lịch sử dụng xe theo ngày
     * 
     * @param vehicleId ID xe
     * @param date ngày xem lịch
     * @return thông tin lịch trong ngày
     */
    CalendarViewResponse getDayView(Long vehicleId, LocalDate date);

    /**
     * Lấy lịch sử dụng xe theo tuần
     * 
     * @param vehicleId ID xe  
     * @param weekStartDate ngày bắt đầu tuần
     * @return danh sách lịch 7 ngày
     */
    List<CalendarViewResponse> getWeekView(Long vehicleId, LocalDate weekStartDate);

    /**
     * Lấy lịch sử dụng xe theo tháng
     * 
     * @param vehicleId ID xe
     * @param year năm
     * @param month tháng (1-12)
     * @return danh sách lịch trong tháng
     */
    List<CalendarViewResponse> getMonthView(Long vehicleId, int year, int month);

    /**
     * Tìm thời gian trống khả dụng
     * 
     * @param vehicleId ID xe
     * @param startDate ngày bắt đầu tìm kiếm
     * @param endDate ngày kết thúc tìm kiếm  
     * @param durationMinutes thời lượng cần (phút)
     * @return danh sách thời gian trống
     */
    List<LocalDateTime> findAvailableSlots(Long vehicleId, LocalDate startDate, 
                                          LocalDate endDate, int durationMinutes);

    /**
     * Kiểm tra conflict với booking khác
     * 
     * @param vehicleId ID xe
     * @param startTime thời gian bắt đầu
     * @param endTime thời gian kết thúc
     * @param excludeBookingId ID booking cần loại trừ (khi update)
     * @return true nếu có conflict
     */
    boolean hasBookingConflict(Long vehicleId, LocalDateTime startTime, 
                              LocalDateTime endTime, Long excludeBookingId);

    /**
     * Lấy utilization rate của xe trong khoảng thời gian
     * 
     * @param vehicleId ID xe
     * @param startDate ngày bắt đầu
     * @param endDate ngày kết thúc
     * @return tỉ lệ sử dụng (0.0 - 1.0)
     */
    double getVehicleUtilizationRate(Long vehicleId, LocalDate startDate, LocalDate endDate);

    /**
     * Lấy next available time cho xe
     * 
     * @param vehicleId ID xe
     * @param fromTime từ thời điểm nào
     * @param minDurationMinutes thời lượng tối thiểu
     * @return thời gian available tiếp theo
     */
    LocalDateTime getNextAvailableTime(Long vehicleId, LocalDateTime fromTime, int minDurationMinutes);

    /**
     * Export calendar sang format iCal
     * 
     * @param vehicleId ID xe
     * @param startDate ngày bắt đầu
     * @param endDate ngày kết thúc
     * @return nội dung iCal
     */
    String exportToICalendar(Long vehicleId, LocalDate startDate, LocalDate endDate);
}