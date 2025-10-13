package com.evcc.evcc.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private UUID memberId;

    @Column(nullable = false)
    private YearMonth period;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "amount_due")),
        @AttributeOverride(name = "currency", column = @Column(name = "currency_due"))
    })
    private Money amountDue;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "amount_paid")),
        @AttributeOverride(name = "currency", column = @Column(name = "currency_paid"))
    })
    private Money amountPaid;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Getters and setters
    // ...existing code...
}