package com.evcc.booking.enums;

/**
 * Enum đại diện cho trạng thái của việc đặt lịch sử dụng xe
 */
public enum BookingStatus {
    PENDING,        // Chờ xác nhận - đặt lịch nhưng chưa được duyệt
    CONFIRMED,      // Đã xác nhận - lịch được chấp thuận
    IN_PROGRESS,    // Đang sử dụng - xe đang được sử dụng theo lịch
    COMPLETED,      // Đã hoàn thành - sử dụng xong, có thể có feedback
    CANCELLED,      // Đã hủy - người đặt hủy hoặc admin hủy
    NO_SHOW,        // Không xuất hiện - không sử dụng xe theo lịch đã đặt
    EXPIRED         // Hết hạn - quá thời gian sử dụng mà không check-in
}