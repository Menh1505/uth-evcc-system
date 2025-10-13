package com.evcc.vehicle.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;



@Entity
@Table(name = "usages")
public class Usage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private UUID bookingId;

    @Column(nullable = false)
    private Long km;

    @Column(nullable = false)
    private BigDecimal kWh;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UsageSource source;

    // Getters and setters
    // ...existing code...
}
