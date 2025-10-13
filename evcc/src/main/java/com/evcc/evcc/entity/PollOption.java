package com.evcc.evcc.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "poll_options")
public class PollOption {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private UUID pollId;

    @Column(nullable = false)
    private String text;

    // Getters and setters
    // ...existing code...
}