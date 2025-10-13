package com.evcc.evcc.repository;

import com.evcc.evcc.entity.PaymentWebhook;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PaymentWebhookRepository extends JpaRepository<PaymentWebhook, UUID> {
}