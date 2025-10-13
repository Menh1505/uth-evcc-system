package com.evcc.poll.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;



@Entity
@Table(name = "poll_votes")
public class PollVote {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private UUID pollId;

    @Column(nullable = false)
    private UUID memberId;

    @Column(nullable = false)
    private UUID optionId;

    @Column(nullable = false)
    private BigDecimal weight;

    @Column(nullable = false)
    private LocalDateTime at;

    // Getters and setters
    // ...existing code...
}
