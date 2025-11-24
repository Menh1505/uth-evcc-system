package com.evcc.voting.enums;

/**
 * Loại vote trong nhóm đồng sở hữu xe
 */
public enum VoteType {
    EXPENSE_APPROVAL, // Phê duyệt chi phí (sửa chữa, nâng cấp)
    FUND_ALLOCATION, // Phân bổ quỹ tài chính
    VEHICLE_PURCHASE, // Mua xe mới
    VEHICLE_SALE, // Bán xe
    GROUP_POLICY, // Thay đổi quy định nhóm
    MEMBER_MANAGEMENT, // Quản lý thành viên
    BUDGET_PLANNING, // Lập ngân sách
    MAINTENANCE_SCHEDULE, // Lịch bảo trì
    OTHER                   // Khác
}
