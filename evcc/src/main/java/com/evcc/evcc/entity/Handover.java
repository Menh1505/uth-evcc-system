package com.evcc.evcc.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "handovers")
public class Handover {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private UUID bookingId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HandoverType type;

    @Column(nullable = false)
    private Long odo;

    @ElementCollection
    private java.util.List<String> photos;

    private String note;

    @Column(nullable = false)
    private LocalDateTime at;

    @Column(nullable = false)
    private String signatureRef;

    // Getters and setters
    // ...existing code...
}