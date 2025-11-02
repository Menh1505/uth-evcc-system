package com.evcc.vehicle.dto;

import lombok.Data;

@Data
public class VehicleResponse {
    private Long id;
    private String licensePlate;
    private String model;
    private String vinNumber;
    private int modelYear;
    private double batteryCapacityKWh;
    // private Long ownerGroupId;
}