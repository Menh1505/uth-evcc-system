package com.evcc.vehicle.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.evcc.group.entity.Group;
import com.evcc.vehicle.enums.VehicleStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity đại diện cho một xe điện trong hệ thống đồng sở hữu. Mỗi xe thuộc về
 * một nhóm và có thể được các thành viên trong nhóm sử dụng.
 *
 * Mô hình đồng sở hữu: - Nhóm góp vốn để mua xe - Xe thuộc sở hữu chung của
 * nhóm - Các thành viên có quyền sử dụng theo lịch trình - Theo dõi chi phí vận
 * hành và bảo trì chung
 */
@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String licensePlate;

    @Column(length = 100)
    private String make;

    @Column(length = 100)
    private String model;

    private Integer year;

    // Co-ownership: many vehicles can belong to one group
    // Mỗi xe thuộc về một nhóm đồng sở hữu
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    // Financial - Investment tracking
    // Thông tin tài chính - theo dõi đầu tư
    @Column(precision = 19, scale = 2)
    private BigDecimal purchasePrice; // Vốn đầu tư ban đầu

    private LocalDate purchaseDate; // Ngày đầu tư

    // EV specific
    private Double batteryCapacity; // kWh

    // Tracking
    private Long initialOdometer;

    // Status
    private VehicleStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
