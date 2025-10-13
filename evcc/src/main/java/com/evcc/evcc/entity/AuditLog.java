package com.evcc.evcc.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private UUID actorId;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String entityType;

    private UUID entityId;

    @Lob
    @Column(nullable = false)
    private String changes; // JSON lưu dưới dạng String

    @Column(nullable = false)
    private LocalDateTime at;

    // Getters and setters
    // ...existing code...
}