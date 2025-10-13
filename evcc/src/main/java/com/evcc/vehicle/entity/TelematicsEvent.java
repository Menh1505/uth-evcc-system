package com.evcc.vehicle.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;



@Entity
@Table(name = "telematics_events")
public class TelematicsEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private UUID vehicleId;

    @Column(nullable = false)
    private LocalDateTime at;

    @Column(nullable = false)
    private Long kmDelta;

    @Column(nullable = false)
    private BigDecimal kWhDelta;

    @Column(nullable = false)
    private String source;

    // Getters and setters
    // ...existing code...
}
