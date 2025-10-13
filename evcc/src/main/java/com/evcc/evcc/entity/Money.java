package com.evcc.evcc.entity;

import jakarta.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
public class Money {
    private BigDecimal amount;
    private String currency;

    // Getters and setters
    // ...existing code...
}