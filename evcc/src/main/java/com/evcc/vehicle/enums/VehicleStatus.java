package com.evcc.vehicle.enums;

/**
 * Trạng thái của xe đồng sở hữu
 */
public enum VehicleStatus {
    AVAILABLE, // Sẵn sàng sử dụng
    IN_USE, // Đang hoạt động
    CHARGING, // Đang sạc
    MAINTENANCE, // Đang bảo trì
    DECOMMISSIONED  // Đã ngừng hoạt động
}
