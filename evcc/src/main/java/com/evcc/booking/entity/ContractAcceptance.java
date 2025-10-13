package com.evcc.booking.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;



@Entity
@Table(name = "contract_acceptances")
public class ContractAcceptance {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private UUID contractId;

    @Column(nullable = false)
    private UUID memberId;

    @Column(nullable = false)
    private LocalDateTime acceptedAt;

    @Column(nullable = false)
    private String signatureRef;

    // Getters and setters
    // ...existing code...
}
