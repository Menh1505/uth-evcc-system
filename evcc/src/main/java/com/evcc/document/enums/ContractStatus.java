package com.evcc.document.enums;

/**
 * Trạng thái của hợp đồng.
 * DRAFT: Mới tạo, đang soạn thảo, có thể chỉnh sửa và thêm người.
 * PENDING_PARTICIPANT_APPROVAL: Đã gửi, chờ các bên ký/xác thực. (KHÔNG THỂ SỬA)
 * PENDING_ADMIN_APPROVAL: Các bên đã ký, chờ Admin duyệt. (KHÔNG THỂ SỬA)
 * ACTIVE: Đã được duyệt, có hiệu lực. (KHÔNG THỂ SỬA)
 * REJECTED: Bị từ chối (bởi người tham gia hoặc Admin).
 */
public enum ContractStatus {
    DRAFT,
    PENDING_PARTICIPANT_APPROVAL,
    PENDING_ADMIN_APPROVAL,
    ACTIVE,
    REJECTED
}