package com.evcc.evcc.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "costs")
public class Cost {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private UUID groupId;

    private UUID vehicleId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CostType type;

    @Embedded
    private Money amount;

    @Column(nullable = false)
    private LocalDateTime occurredAt;

    private String notes;

    // Getters and setters
    // ...existing code...
}