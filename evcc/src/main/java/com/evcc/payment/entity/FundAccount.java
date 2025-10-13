package com.evcc.payment.entity;

import com.evcc.common.entity.Money;

import jakarta.persistence.*;
import java.util.UUID;



@Entity
@Table(name = "fund_accounts")
public class FundAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private UUID groupId;

    @Embedded
    private Money balance;

    // Getters and setters
    // ...existing code...
}
