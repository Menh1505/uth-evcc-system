package com.evcc.evcc.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fund_txns")
public class FundTxn {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private UUID fundId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FundTxnType type;

    @Embedded
    private Money amount;

    @Column(nullable = false)
    private UUID requestedBy;

    private UUID approvedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TxnStatus status;

    private String notes;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime postedAt;

    // Getters and setters
    // ...existing code...
}