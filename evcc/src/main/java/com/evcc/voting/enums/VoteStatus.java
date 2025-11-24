package com.evcc.voting.enums;

/**
 * Trạng thái của cuộc vote
 */
public enum VoteStatus {
    DRAFT, // Bản nháp (chưa bắt đầu)
    ACTIVE, // Đang diễn ra
    CLOSED, // Đã kết thúc
    CANCELLED, // Đã hủy
    APPROVED, // Đã được phê duyệt (có kết quả)
    REJECTED    // Đã bị từ chối (có kết quả)
}
