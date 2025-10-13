package com.evcc.evcc.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "cost_shares")
public class CostShare {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private UUID costId;

    @Column(nullable = false)
    private UUID memberId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AllocationBasis basis;

    @Column(nullable = false)
    private BigDecimal ratio;

    @Embedded
    private Money amount;

    // Getters and setters
    // ...existing code...
}