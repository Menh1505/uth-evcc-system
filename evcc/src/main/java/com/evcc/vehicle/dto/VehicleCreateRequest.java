package com.evcc.vehicle.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * DTO dùng để tạo/ cập nhật vehicle
 */
public class VehicleCreateRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String licensePlate;

    private String make;

    private String model;

    private Integer year;

    @NotNull
    private Long groupId;

    @PositiveOrZero
    private BigDecimal purchasePrice;

    private LocalDate purchaseDate;

    private Double batteryCapacity;

    private Long initialOdometer;

    private String status; // optional, will be parsed to enum in service

    // getters/setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }
    public BigDecimal getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(BigDecimal purchasePrice) { this.purchasePrice = purchasePrice; }
    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }
    public Double getBatteryCapacity() { return batteryCapacity; }
    public void setBatteryCapacity(Double batteryCapacity) { this.batteryCapacity = batteryCapacity; }
    public Long getInitialOdometer() { return initialOdometer; }
    public void setInitialOdometer(Long initialOdometer) { this.initialOdometer = initialOdometer; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
