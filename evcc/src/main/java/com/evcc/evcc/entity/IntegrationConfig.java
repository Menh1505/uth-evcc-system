package com.evcc.evcc.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "integration_configs")
public class IntegrationConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IntegrationType type;

    @Column(nullable = false)
    private String provider;

    @Lob
    @Column(nullable = false)
    private String config; // JSON lưu dưới dạng String

    @Column(nullable = false)
    private Boolean enabled;

    // Getters and setters
    // ...existing code...
}