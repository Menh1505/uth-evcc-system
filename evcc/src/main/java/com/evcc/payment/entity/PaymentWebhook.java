package com.evcc.payment.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;



@Entity
@Table(name = "payment_webhooks")
public class PaymentWebhook {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private UUID paymentId;

    @Column(nullable = false)
    private String event;

    @Lob
    @Column(nullable = false)
    private String payload; // JSON lưu dưới dạng String

    @Column(nullable = false)
    private LocalDateTime receivedAt;

    // Getters and setters
    // ...existing code...
}
