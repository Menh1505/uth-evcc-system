package com.evcc.poll.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;



@Entity
@Table(name = "polls")
public class Poll {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private UUID groupId;

    @Column(nullable = false)
    private String topic;

    private String description;

    @Column(nullable = false)
    private LocalDateTime deadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PollRule rule;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PollStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Getters and setters
    // ...existing code...
}
