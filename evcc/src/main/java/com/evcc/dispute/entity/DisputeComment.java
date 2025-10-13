package com.evcc.dispute.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;



@Entity
@Table(name = "dispute_comments")
public class DisputeComment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private UUID disputeId;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime at;

    // Getters and setters
    // ...existing code...
}
