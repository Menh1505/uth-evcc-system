package com.evcc.vehicle.dto;

import lombok.Data;

@Data
public class VehicleCreateRequest {
    private String licensePlate;
    private String model;
    private String vinNumber;
    private int modelYear;
    private double batteryCapacityKWh;
    // private Long ownerGroupId; // Sẽ dùng khi có module 'group'
}