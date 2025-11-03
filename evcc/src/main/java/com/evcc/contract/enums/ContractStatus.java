package com.evcc.contract.enums;

/**
 * Enum đại diện cho trạng thái của hợp đồng mua xe
 */
public enum ContractStatus {
    DRAFT,      // Bản nháp - đang soạn thảo
    PENDING,    // Chờ ký - đã hoàn thành thông tin, chờ các thành viên ký
    ACTIVE,     // Có hiệu lực - tất cả thành viên đã ký và xe đã được mua
    COMPLETED,  // Hoàn thành - hợp đồng đã kết thúc (xe được bán, chuyển nhượng...)
    CANCELLED   // Đã hủy - hợp đồng bị hủy bỏ
}